package com.droidclan.samplefoodblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private Uri post_image_uri;
    private ImageView post_image;
    private EditText post_title, post_details;
    private ProgressDialog pdialog;
    private FirebaseAuth mAuth;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //Finding View By Id
        mAuth = FirebaseAuth.getInstance();
        TextView header = findViewById(R.id.header);
        CircleImageView profileimage = findViewById(R.id.profileimage);
        ImageView search = findViewById(R.id.search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        post_title = findViewById(R.id.post_title);
        post_details = findViewById(R.id.post_details);
        post_image = findViewById(R.id.post_image);
        Button submit_post_btn = findViewById(R.id.submit_post_btn);

        submit_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allClear()){
                    uploadPostImage(Html.fromHtml(post_title.getText().toString()).toString());
                }
            }
        });

        //Customizing toolbar
        Typeface pacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        header.setText("Post Blog");
        header.setTypeface(pacifico);
        profileimage.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //Setting post image to imageview
        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(PostActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PostActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        pickimage();
                    }
                } else {
                    pickimage();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {

            if (data != null) {
                post_image_uri = data.getData();
                post_image.setImageURI(post_image_uri);
            } else {
                Toast.makeText(this, "Unable to load Image", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void pickimage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private boolean allClear() {
        if (post_image_uri == null) {
            toast("Please select an Image");
            return false;
        }else if (TextUtils.isEmpty(post_title.getText().toString())){
            post_title.setError("Title can't be Empty");
            return false;
        }else if (TextUtils.isEmpty(post_details.getText().toString())){
            post_details.setError("Detail can't be Empty");
            return false;
        }else {
            return true;
        }
    }

    private void uploadPostImage(final String postId) {
        pdialog = new ProgressDialog(PostActivity.this, R.style.MyAlertDialogStyle);
        pdialog.setMessage("Please wait...");
        pdialog.setIndeterminate(true);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.setCancelable(false);
        pdialog.show();
        StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("post_images/" + postId + ".png");
        riversRef.putFile(post_image_uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            savePostDetails(task.getResult().getDownloadUrl().toString());
                        } else {
                            Toast.makeText(PostActivity.this, "Unable to upload Image ! " + task.getException(), Toast.LENGTH_SHORT).show();
                            pdialog.dismiss();
                        }
                    }
                });
    }

    private void savePostDetails(String imageUrl) {
        String title = post_title.getText().toString();
        String details = post_details.getText().toString();
        String desc = details.substring(0, Math.min(details.length(), 250));
        String user = mAuth.getCurrentUser().getUid();

        Map<String, Object> post = new HashMap<>();
        post.put("User", user);
        post.put("Views", 0);
        post.put("Image", imageUrl);
        post.put("Time", System.currentTimeMillis());
        post.put("Title", title);
        post.put("Desc", desc);
        post.put("Details", details);

        FirebaseFirestore.getInstance().collection("Posts").document(title)
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pdialog.dismiss();
                        post_title.setText("");
                        post_details.setText("");
                        post_image.setImageResource(R.color.com_facebook_device_auth_text);
                        Toast.makeText(PostActivity.this, "Post uploaded successfully :)", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong :(", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void toast(String message){
        Toast.makeText(PostActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
