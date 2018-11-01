package com.droidclan.samplefoodblog;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class WelcomeScreenActivity extends AppCompatActivity {

    TextView toptext, quotetext;
    private FirebaseAuth mAuth;
    Button signup, signin;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(WelcomeScreenActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Typeface pacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");


        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        quotetext = findViewById(R.id.quotetext);
        toptext = findViewById(R.id.toptext);
        toptext.setTypeface(pacifico);
        quotetext.setTypeface(pacifico);

        showquotes();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeScreenActivity.this, LoginActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeScreenActivity.this, RegisterActivity.class));
            }
        });
    }

    private void showquotes(){
        String quote1 ="Nothing is better than going home to family and eating good food and relaxing";
        String quote2 ="One cannot think well, love well, sleep well, if one has not dined well";
        String quote3 ="Your diet is a bank account. Good food choices are good investments";
        String quote4 ="Spaghetti can be eaten most successfully if you inhale it like a vacuum cleaner";
        String quote5 ="If music be the food of love, play on";

        String[] quotes = {quote1,quote2,quote3,quote4,quote5};
        Random r = new Random();
        int n = r.nextInt(5);
        quotetext.setText("\""+quotes[n]+"\"");

    }
}
