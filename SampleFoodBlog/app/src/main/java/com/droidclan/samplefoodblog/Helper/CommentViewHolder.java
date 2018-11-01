package com.droidclan.samplefoodblog.Helper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.droidclan.samplefoodblog.ProfileActivity;
import com.droidclan.samplefoodblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    View view;
    String user;
    private TextView comment_details;

    public CommentViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        comment_details = view.findViewById(R.id.comment_details);

    }

    public void setUserImage(final String user_id, final Activity ctx){
        final CircleImageView user_image = view.findViewById(R.id.user_image);

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(ctx, ProfileActivity.class);
                profile.putExtra("UserId", user_id);
                ctx.startActivity(profile);
            }
        });

        comment_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(ctx, ProfileActivity.class);
                profile.putExtra("UserId", user_id);
                ctx.startActivity(profile);
            }
        });

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(user_id);
        documentReference.get().addOnCompleteListener(ctx, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Glide.with(ctx)
                            .applyDefaultRequestOptions(new RequestOptions()
                                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                                    .error(R.drawable.com_facebook_profile_picture_blank_square)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL))
                            .load(task.getResult().get("url").toString())
                            .into(user_image);
                }
            }
        });
    }

    public void setComment(String comment){
        TextView user_comment = view.findViewById(R.id.user_comment);
        user_comment.setText(comment);
    }

    public void setDetails(String name, long time){
        TimeFormatter timeFormatter = new TimeFormatter();
        String details = name + "  •  " + timeFormatter.getTime(time);
        comment_details.setText(details);
    }

    public void setDeleteBtn(final String user_id, final String post_id, final String comment, final long time){
        ImageView delete_comment = view.findViewById(R.id.delete_comment);

        if (System.currentTimeMillis()-time <= 300000){
            if (user.equals(user_id)){
                delete_comment.setVisibility(View.VISIBLE);
                delete_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore.getInstance().collection("Posts").document(post_id).collection("Comments")
                                .document(time+user_id).delete();
                    }
                });
            }else {
                delete_comment.setVisibility(View.GONE);
            }
        }else {
            delete_comment.setVisibility(View.GONE);
        }
    }
}
