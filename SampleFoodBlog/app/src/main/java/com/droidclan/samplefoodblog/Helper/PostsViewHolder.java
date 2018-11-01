package com.droidclan.samplefoodblog.Helper;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.droidclan.samplefoodblog.BlogDetails;
import com.droidclan.samplefoodblog.R;


public class PostsViewHolder extends RecyclerView.ViewHolder {

    private View view;
    private ImageView post_image;

    public PostsViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        post_image = view.findViewById(R.id.post_image);
    }

    public void showPostDetails(String imgurl, String user, long time, String title, String details, int views, final Activity activity){

        Glide.with(activity)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.color.com_facebook_device_auth_text)
                        .error(R.color.com_facebook_device_auth_text)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(imgurl)
                .into(post_image);


        final Intent post = new Intent(activity, BlogDetails.class);
        post.putExtra("ImageUrl", imgurl);
        post.putExtra("Time", time);
        post.putExtra("User", user);
        post.putExtra("Title", title);
        post.putExtra("Details", details);
        post.putExtra("Views", views);

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(post);
            }
        });
    }
}
