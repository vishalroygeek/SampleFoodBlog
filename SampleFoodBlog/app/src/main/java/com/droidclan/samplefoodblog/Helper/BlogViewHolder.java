package com.droidclan.samplefoodblog.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.droidclan.samplefoodblog.BlogDetails;
import com.droidclan.samplefoodblog.MainActivity;
import com.droidclan.samplefoodblog.ProfileActivity;
import com.droidclan.samplefoodblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import static com.facebook.FacebookSdk.getApplicationContext;

public class BlogViewHolder extends RecyclerView.ViewHolder {

    View view;
    FirebaseAuth mAuth;
    String user_id;
    TextView view_count;
    ImageView views_icon;

    public BlogViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        mAuth = FirebaseAuth.getInstance();
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        view_count = view.findViewById(R.id.view_count);
        views_icon = view.findViewById(R.id.views_icon);
        view_count.setVisibility(View.INVISIBLE);
        views_icon.setVisibility(View.INVISIBLE);
    }

    public void setImage(String url, Context ctx){
        final ImageView post_image = view.findViewById(R.id.post_image);
        Glide.with(ctx)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.color.com_facebook_device_auth_text)
                        .error(R.color.com_facebook_device_auth_text)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(url)
                .into(post_image);

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view_count.getVisibility()==View.VISIBLE){
                    post_image.setColorFilter(null);
                    view_count.setVisibility(View.INVISIBLE);
                    views_icon.setVisibility(View.INVISIBLE);
                }else {
                    post_image.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
                    view_count.setVisibility(View.VISIBLE);
                    views_icon.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void setView(int views){
        view_count.setText(String.valueOf(views));
    }

    public void setUser(Activity activity,final String user_id, final Context ctx){
        final TextView username = view.findViewById(R.id.user_name);
        final CircleImageView userimage = view.findViewById(R.id.profile_image);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(ctx, ProfileActivity.class);
                profile.putExtra("UserId", user_id);
                ctx.startActivity(profile);
            }
        });

        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(ctx, ProfileActivity.class);
                profile.putExtra("UserId", user_id);
                ctx.startActivity(profile);
            }
        });

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(activity, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String name = task.getResult().get("name").toString();
                    String url = task.getResult().get("url").toString();
                    username.setText(name);
                    Glide.with(ctx)
                            .applyDefaultRequestOptions(new RequestOptions()
                                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                                    .error(R.drawable.com_facebook_profile_picture_blank_square)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL))
                            .load(url)
                            .into(userimage);
                }else {
                    username.setText("");
                    userimage.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
                }
            }
        });

    }

    public void setDate(long time){
        TextView post_date = view.findViewById(R.id.date);
        TimeFormatter timeFormatter = new TimeFormatter();
        post_date.setText(timeFormatter.getTime(time));
    }

    public void setTitle(String title){
        TextView head = view.findViewById(R.id.post_title);
        head.setText(title);
    }

    public void setDesc(String desc){
        TextView post_desc = view.findViewById(R.id.post_desc);
        post_desc.setText(desc);
    }

    public void setLikes(final Activity activity, String post_title){
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_in);
        final Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_out);



        final TextView like_count = view.findViewById(R.id.like_count);
        final ImageView like_btn = view.findViewById(R.id.like_btn);

        final CollectionReference collection = FirebaseFirestore.getInstance().collection("Posts").document(post_title)
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

        collection.addSnapshotListener(activity, likeEvent);

        collection.document(user_id).addSnapshotListener(activity, checkifLiked);

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

    public void setBookmark(final Activity activity, final String post_title, final String post_desc, final String post_img, final long post_time, final CoordinatorLayout coordinatorLayout, final String user_id){

        final ImageView bookmark_btn = view.findViewById(R.id.bookmark_btn);

        final CollectionReference collection = FirebaseFirestore.getInstance().collection("Users").document(mAuth.getCurrentUser().getUid())
                .collection("Bookmarks");


        collection.document(post_title).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
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
                collection.document(post_title).get().addOnCompleteListener(activity,new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                collection.document(post_title).delete();
                            }else {
                                Map<String, Object> bookmark = new HashMap<>();
                                bookmark.put("Title",post_title);
                                bookmark.put("Time", post_time);
                                bookmark.put("Desc", post_desc);
                                bookmark.put("Image", post_img);
                                bookmark.put("User", user_id);
                                bookmark.put("BookmarkTime", System.currentTimeMillis());
                                collection.document(post_title)
                                        .set(bookmark);

                                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                        "Post Bookmarked", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public void showPostDetails(String imgurl, String user, long time, String title, String details, int views, final Activity activity){

        TextView post_title = view.findViewById(R.id.post_title);
        TextView post_desc = view.findViewById(R.id.post_desc);

        final Intent post = new Intent(activity, BlogDetails.class);
        post.putExtra("ImageUrl", imgurl);
        post.putExtra("Time", time);
        post.putExtra("User", user);
        post.putExtra("Title", title);
        post.putExtra("Details", details);
        post.putExtra("Views", views);

        post_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(post);
            }
        });

        post_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(post);
            }
        });
    }
}