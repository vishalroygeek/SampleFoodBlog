package com.droidclan.samplefoodblog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.droidclan.samplefoodblog.Helper.BlogViewHolder;
import com.droidclan.samplefoodblog.Helper.CommentViewHolder;
import com.droidclan.samplefoodblog.Helper.TimeFormatter;
import com.droidclan.samplefoodblog.Modal.Blog;
import com.droidclan.samplefoodblog.Modal.Comment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BlogDetails extends AppCompatActivity {

    private ImageView post_image, user_image, bookmark_btn, close_btn, like_btn, comment_btn, share_btn;
    private TextView user_name, date, post_title, post_desc, like_count, comment_count;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private Bundle bundle;
    private FirestoreRecyclerAdapter<Comment,CommentViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        //Finding view by id
        post_image = findViewById(R.id.post_image);
        user_image = findViewById(R.id.user_image);
        user_name = findViewById(R.id.user_name);
        date = findViewById(R.id.date);
        post_title = findViewById(R.id.post_title);
        post_desc = findViewById(R.id.post_desc);
        bookmark_btn = findViewById(R.id.bookmark_btn);
        close_btn = findViewById(R.id.close_btn);
        comment_btn = findViewById(R.id.comment_btn);
        like_btn = findViewById(R.id.like_btn);
        like_count = findViewById(R.id.like_count);
        comment_count = findViewById(R.id.comment_count);
        //TODO : Initialize this share button
        share_btn = findViewById(R.id.share_btn);

        // Getting post details
        bundle = getIntent().getExtras();
        String ImageUrl = bundle.getString("ImageUrl");
        String User = bundle.getString("User");
        long Time = bundle.getLong("Time");
        String Title = bundle.getString("Title");
        String Details = bundle.getString("Details");
        int views = bundle.getInt("Views");

        //Setting some other methods
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getCurrentUser().getUid();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlogDetails.super.onBackPressed();
            }
        });
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentsDialog();
            }
        });


        //Displaying number of comments
        CollectionReference collection = FirebaseFirestore.getInstance().collection("Posts").document(Title)
                .collection("Comments");
        EventListener<QuerySnapshot> comments = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                String comments = String.valueOf(documentSnapshots.getDocuments().size());

                if (comments.equals("0")){
                    comment_count.setText("");
                }else {
                    comment_count.setText(comments);
                }
            }
        };
        collection.addSnapshotListener(comments);



        // Making dark Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        //Setting all post details
        setPostImage(ImageUrl);
        setUser(User);
        setTime(Time);
        post_title.setText(Title);
        post_desc.setText(Details);
        setBookmark(Title, user);
        setViews(views, Title);
        setLikes(user, Title);

    }

    private void setTime(long time){
        TimeFormatter timeFormatter = new TimeFormatter();
        date.setText(timeFormatter.getTime(time));
    }

    private void setPostImage(String url){
        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.color.com_facebook_device_auth_text)
                        .error(R.color.com_facebook_device_auth_text)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url)
                .into(post_image);
    }

    private void setUser(final String user_id){

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String name = task.getResult().get("name").toString();
                    String url = task.getResult().get("url").toString();
                    user_name.setText(name);
                    Glide.with(getApplicationContext())
                            .applyDefaultRequestOptions(new RequestOptions()
                                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                                    .error(R.drawable.com_facebook_profile_picture_blank_square)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL))
                            .load(url)
                            .into(user_image);

                    user_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profile = new Intent(BlogDetails.this, ProfileActivity.class);
                            profile.putExtra("UserId", user_id);
                            BlogDetails.this.startActivity(profile);
                        }
                    });

                    user_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent profile = new Intent(BlogDetails.this, ProfileActivity.class);
                            profile.putExtra("UserId", user_id);
                            BlogDetails.this.startActivity(profile);
                        }
                    });
                }else {
                    user_name.setText("");
                    user_image.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
                }
            }
        });

    }

    private void setBookmark(final String post_id, String user_id){

        final CollectionReference collection = FirebaseFirestore.getInstance().collection("Users").document(user_id)
                .collection("Bookmarks");

            collection.document(post_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()){
                        bookmark_btn.setImageResource(R.drawable.bookmarked);
                    }else {
                        bookmark_btn.setImageResource(R.drawable.bookmark);
                    }
                }
            });


        bookmark_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collection.document(post_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                collection.document(post_id).delete();
                            } else {
                                Map<String, Object> bookmark = new HashMap<>();
                                bookmark.put("Post", post_id);
                                bookmark.put("Time",System.currentTimeMillis());
                                collection.document(post_id)
                                        .set(bookmark);

                                Toast.makeText(BlogDetails.this, "Post Bookmarked", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


        });

    }

    private void setViews(final int views, String post_id){
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Posts").document(post_id);

        int totalviews = views+1;

        documentReference.update("Views", totalviews)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Log.d("Views Error", "Error : " + task.getException());
                        }
                    }
                });


    }

    private void setLikes(final String user_id, String post_id){
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
        final Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out);


        final CollectionReference collection = FirebaseFirestore.getInstance().collection("Posts").document(post_id)
                .collection("Likes");


        final EventListener<DocumentSnapshot> checkifLiked = new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (documentSnapshot.exists()){
                    like_btn.setImageResource(R.drawable.liked);
                }else {
                    like_btn.setImageResource(R.drawable.unliked);
                }

            }
        };

        final EventListener<QuerySnapshot> likeEvent = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                String likes = String.valueOf(documentSnapshots.getDocuments().size());
                if (likes.equals("0")){
                    like_count.setText("");
                }else {
                    like_count.setText(likes);
                }
            }
        };

        collection.addSnapshotListener(likeEvent);

        collection.document(user_id).addSnapshotListener(checkifLiked);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                like_btn.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                collection.document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                collection.document(user_id).delete();

                            }else {
                                like_btn.startAnimation(animation);
                                final MediaPlayer sound = MediaPlayer.create(getApplicationContext(), R.raw.like_btn_click);
                                sound.start();
                                Map<String, Object> like = new HashMap<>();
                                like.put("UserId",user_id);
                                collection.document(user_id)
                                        .set(like)
                                        .addOnCompleteListener(
                                                new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        sound.stop();
                                                    }
                                                }
                                        );
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showCommentsDialog(){
        //Customizing & creating the dialog here
        Dialog commentdialog = new Dialog(this, R.style.Comment_Dialog_Theme);
        commentdialog.setContentView(R.layout.comment_dialog_layout);
        commentdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        commentdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        commentdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                adapter.stopListening();
            }
        });

        //Finding view by id here
        CircleImageView profile_image = commentdialog.findViewById(R.id.profile_image);
        ImageView send_comment = commentdialog.findViewById(R.id.send_comment);
        final EditText comment_input = commentdialog.findViewById(R.id.comment_input);
        final RecyclerView comment_list = commentdialog.findViewById(R.id.comment_list);
        RelativeLayout error = commentdialog.findViewById(R.id.error);

        //Loading current user profile image
        Glide.with(this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                        .error(R.drawable.com_facebook_profile_picture_blank_square)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(mAuth.getCurrentUser().getPhotoUrl())
                .into(profile_image);

        //Inserting comment to Database
        send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(comment_input.getText().toString())){
                    insertComment(comment_input.getText().toString(), comment_input);
                }
            }
        });

        //Populating comments to recyclerView
        populateComment(comment_input, error, comment_list);
        comment_list.setAdapter(adapter);
        comment_list.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        comment_list.setLayoutManager(linearLayoutManager);
        adapter.startListening();

        //Checking if no comments
        if (adapter.getItemCount() == 0){
            comment_input.setHint("Be the first to comment...");
            error.setVisibility(View.VISIBLE);
        }else {
            comment_input.setHint("Say something...");
            error.setVisibility(View.GONE);
        }

        commentdialog.show();
    }

    private void populateComment(final EditText comment_input, final RelativeLayout error, final RecyclerView comment_list){
        Query query = FirebaseFirestore.getInstance()
                .collection("Posts").document(bundle.getString("Title")).collection("Comments")
                .orderBy("Time", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Comment, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {

                holder.setUserImage(model.getUser(), BlogDetails.this);
                holder.setComment(model.getComment());
                holder.setDetails(model.getName(), model.getTime());
                holder.setDeleteBtn(model.getUser(), bundle.getString("Title"), model.getComment(), model.getTime());

            }

            @Override
            public CommentViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.comment_layout, group, false);
                return new CommentViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                if (adapter.getItemCount() == 0){
                    comment_input.setHint("Be the first to comment...");
                    error.setVisibility(View.VISIBLE);
                }else {
                    comment_input.setHint("Say something...");
                    error.setVisibility(View.GONE);
                }
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                comment_list.smoothScrollToPosition(adapter.getItemCount()-1);
            }
        });
    }

    private void insertComment(String comment, final EditText comment_input){

        comment_input.setText("");
        MediaPlayer sound = MediaPlayer.create(getApplicationContext(), R.raw.comment_sound);
        sound.start();

        long current_time = System.currentTimeMillis();
        String user_id = mAuth.getCurrentUser().getUid();

        Map<String, Object> post = new HashMap<>();
        post.put("Comment", comment);
        post.put("Time", current_time);
        post.put("User", user_id);
        post.put("Name", mAuth.getCurrentUser().getDisplayName());


        FirebaseFirestore.getInstance().collection("Posts").document(bundle.getString("Title"))
                .collection("Comments").document(current_time+user_id)
                .set(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(BlogDetails.this, "Unable to comment !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
