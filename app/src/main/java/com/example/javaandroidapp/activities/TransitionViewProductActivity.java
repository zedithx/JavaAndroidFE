package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.modals.Listing;

public class TransitionViewProductActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transition_product_layout);
        Listing listing = (Listing) getIntent().getSerializableExtra("listing");

        // Handle item click, e.g., start a new activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TransitionViewProductActivity.this, ViewProductActivity.class);
                intent.putExtra("listing", listing);
                startActivity(intent);
                finish();
            }
        }, 500);

    }
}
