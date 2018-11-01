package com.droidclan.samplefoodblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.droidclan.samplefoodblog.Fragments.BookmarksFragment;
import com.droidclan.samplefoodblog.Fragments.HomeFragment;
import com.droidclan.samplefoodblog.Fragments.ActivityFragment;
import com.droidclan.samplefoodblog.Fragments.TrendingFragment;
import com.droidclan.samplefoodblog.Helper.BottomNavigationViewHelper;
import com.droidclan.samplefoodblog.Helper.CustomTypefaceSpan;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    TextView header;
    FirebaseAuth mAuth;
    BottomNavigationView bottom_nav;
    private CircleImageView profileimage;
    private Fragment homeFragment,trendingFragment,bookmarksFragment,activityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();


        //Customizing the Toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Typeface pacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        header = findViewById(R.id.header);
        header.setTypeface(pacifico);
        profileimage = findViewById(R.id.profileimage);
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                profile.putExtra("UserId", mAuth.getCurrentUser().getUid());
                startActivity(profile);
            }
        });
        loadprofileImage(mAuth.getCurrentUser().getPhotoUrl());
        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });


        //Customizing Bottom Navigation
        bottom_nav = findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.disableShiftMode(bottom_nav);
        Menu m = bottom_nav.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            applyFontToMenuItem(mi);
        }
        bottom_nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Initializing Home Fragment
        homeFragment = new HomeFragment();
        trendingFragment = new TrendingFragment();
        bookmarksFragment = new BookmarksFragment();
        activityFragment = new ActivityFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment)
                .add(R.id.fragment_container,trendingFragment)
                .add(R.id.fragment_container,bookmarksFragment)
                .add(R.id.fragment_container,activityFragment)
                .hide(trendingFragment)
                .hide(bookmarksFragment)
                .hide(activityFragment)
                .commit();

    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(homeFragment, trendingFragment, bookmarksFragment, activityFragment);
                    return true;
                case R.id.trending:
                    replaceFragment(trendingFragment, homeFragment, bookmarksFragment, activityFragment);
                    return true;
                case R.id.bookmarks:
                    replaceFragment(bookmarksFragment, homeFragment,trendingFragment,activityFragment);
                    return true;
                case R.id.activity:
                    replaceFragment(activityFragment,homeFragment,trendingFragment,bookmarksFragment);
                    return true;
            }
            return false;
        }
    };

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void loadprofileImage(Uri uri){
        Glide.with(MainActivity.this)
                .applyDefaultRequestOptions(new RequestOptions()
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .error(R.drawable.com_facebook_profile_picture_blank_square)
                .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(uri)
                .into(profileimage);
    }

    private void replaceFragment(Fragment one, Fragment two, Fragment three, Fragment four){
        if (!one.isVisible()){
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .show(one)
                    .hide(two)
                    .hide(three)
                    .hide(four)
                    .commit();
        }

    }
}