package com.droidclan.samplefoodblog;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.droidclan.samplefoodblog.Helper.BlogViewHolder;
import com.droidclan.samplefoodblog.Modal.Blog;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {

    CoordinatorLayout coordinatorLayout;
    Toolbar toolbar;
    ImageView back;
    EditText search_input;
    RecyclerView blog_list;
    private FirestoreRecyclerAdapter<Blog,BlogViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Finding view by id
        coordinatorLayout = findViewById(R.id.constraint_layout);
        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.back);
        search_input = findViewById(R.id.search_input);
        blog_list = findViewById(R.id.blog_list);


        //Initializing the recycler view



        //Customizing the action bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        search_input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (search_input.getRight() - search_input.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        search_input.setText("");
                    }
                }
                return false;
            }
        });

        search_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = search_input.getText().toString();
                    if (!text.equals("")){
                        populateBlogList(text);
                        blog_list.setAdapter(adapter);
                        blog_list.setHasFixedSize(true);
                        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
                        blog_list.setLayoutManager(linearLayoutManager);
                        adapter.startListening();
                    }
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.super.onBackPressed();
            }
        });
    }


    private void populateBlogList(String search_text){


        Query query = FirebaseFirestore.getInstance()
                .collection("Posts")
                .orderBy("Title")
                .orderBy("Time", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("Title", search_text);
//                .startAt(search_text)
//                .endAt(search_text + "\uf8ff");


        FirestoreRecyclerOptions<Blog> options = new FirestoreRecyclerOptions.Builder<Blog>()
                .setQuery(query, Blog.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<Blog, BlogViewHolder>(options) {
            @Override
            public void onBindViewHolder(BlogViewHolder holder, int position, final Blog model) {

                holder.setImage(model.getImage(),SearchActivity.this);
                holder.setUser(SearchActivity.this, model.getUser(), SearchActivity.this);
                holder.setDate(model.getTime());
                holder.setTitle(model.getTitle());
                holder.setDesc(model.getDesc());
                holder.setLikes(SearchActivity.this, model.getTitle());
                holder.setBookmark(SearchActivity.this, model.getTitle(), model.getDesc(), model.getImage(), model.getTime(), coordinatorLayout, model.getUser());
                holder.setView(model.getViews());
                holder.showPostDetails(model.getImage(), model.getUser(), model.getTime(), model.getTitle(), model.getDetails(), model.getViews(), SearchActivity.this);

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
