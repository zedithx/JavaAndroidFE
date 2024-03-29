package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.objects.Listing;

public class TransitionLandingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transition_layout);
        // Handle item click, e.g., start a new activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TransitionLandingActivity.this, LandingActivity.class);
                startActivity(intent);
                finish();
            }
        }, 500);

    }
}
