package com.droidclan.samplefoodblog.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.IconMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droidclan.samplefoodblog.BlogDetails;
import com.droidclan.samplefoodblog.Helper.BlogViewHolder;
import com.droidclan.samplefoodblog.Modal.Blog;
import com.droidclan.samplefoodblog.PostActivity;
import com.droidclan.samplefoodblog.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {

    private RecyclerView blog_list;
    private FirestoreRecyclerAdapter<Blog,BlogViewHolder> adapter;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initializing the Recycler View
        blog_list = view.findViewById(R.id.blog_list);
        populateBlogList();
        blog_list.setAdapter(adapter);
        blog_list.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        blog_list.setLayoutManager(linearLayoutManager);
        coordinatorLayout = view.findViewById(R.id.main_layout);
        adapter.startListening();

        return view;
    }


    // Populating Blog List
    private void populateBlogList(){


        Query query = FirebaseFirestore.getInstance()
                .collection("Posts")
                .orderBy("Time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Blog> options = new FirestoreRecyclerOptions.Builder<Blog>()
                .setQuery(query, Blog.class)
                .build();

        adapter =  new FirestoreRecyclerAdapter<Blog, BlogViewHolder>(options) {
            @Override
            public void onBindViewHolder(BlogViewHolder holder, int position, final Blog model) {

                holder.setImage(model.getImage(),getActivity());
                holder.setUser(getActivity(), model.getUser(), getActivity());
                holder.setDate(model.getTime());
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDesc());
                holder.setLikes(getActivity(),model.getTitle());
                holder.setBookmark(getActivity(),model.getTitle(), model.getDesc(), model.getImage(), model.getTime(), coordinatorLayout, model.getUser());
                holder.setView(model.getViews());
                holder.showPostDetails(model.getImage(), model.getUser(), model.getTime(), model.getTitle(), model.getDetails(), model.getViews(), getActivity());
            }

            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.blog_row, group, false);
                return new BlogViewHolder(view);
            }
        };
    }
}
