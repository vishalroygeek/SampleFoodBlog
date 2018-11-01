package com.droidclan.samplefoodblog.Fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.droidclan.samplefoodblog.Helper.BookmarksViewHolder;
import com.droidclan.samplefoodblog.Modal.Bookmarks;
import com.droidclan.samplefoodblog.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class BookmarksFragment extends Fragment {

    private RecyclerView blog_list;
    private FirestoreRecyclerAdapter<Bookmarks,BookmarksViewHolder> adapter;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth mAuth;
    private String user_id;
    private RelativeLayout error;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        error = view.findViewById(R.id.error);
        error.setVisibility(View.GONE);

        //Initializing the Recycler View
        blog_list = view.findViewById(R.id.blog_list);
        populateBlogList();
        blog_list.setAdapter(adapter);
        blog_list.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        blog_list.setLayoutManager(linearLayoutManager);
        coordinatorLayout = view.findViewById(R.id.main_layout);
        adapter.startListening();

        //Checking if no bookmarks
        if (adapter.getItemCount() == 0){
            error.setVisibility(View.VISIBLE);
        }else {
            error.setVisibility(View.GONE);
        }

        return view;
    }


    private void populateBlogList(){


        Query query = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user_id)
                .collection("Bookmarks")
                .orderBy("BookmarkTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Bookmarks> options = new FirestoreRecyclerOptions.Builder<Bookmarks>()
                .setQuery(query, Bookmarks.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Bookmarks, BookmarksViewHolder>(options) {
            @Override
            public void onBindViewHolder(BookmarksViewHolder holder, int position, final Bookmarks model) {

                holder.setImage(model.getImage(),getActivity());
                holder.setUser(getActivity(), model.getUser());
                holder.setDate(model.getTime());
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDesc());
                holder.setLikes(model.getTitle());
                holder.setBookmark(model.getTitle(), coordinatorLayout);
                holder.setView(model.getTitle());
                holder.showPostDetails(model.getImage(), model.getUser(), model.getTime(), model.getTitle(), getActivity());
            }

            @Override
            public BookmarksViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.blog_row, group, false);
                return new BookmarksViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                if (adapter.getItemCount() == 0){
                    error.setVisibility(View.VISIBLE);
                }else {
                    error.setVisibility(View.GONE);
                }
            }
        };
    }
}
