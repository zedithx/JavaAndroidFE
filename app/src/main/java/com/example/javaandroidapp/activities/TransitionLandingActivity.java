package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.utils.Listings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;

public class TransitionLandingActivity extends AppCompatActivity {
    private List<Listing> listings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transition_layout);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Listings.getAllListings(db, "All", new CallbackAdapter() {
            @Override
            public void getList(List<Listing> listings_new) {
                if (listings_new.size() != 0) {
                    listings = listings_new;
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Intent only after listings have been stored in static
                        Intent intent = new Intent(TransitionLandingActivity.this, LandingActivity.class);
                        //pass listings to landingactivity
                        intent.putExtra("listings", (Serializable) listings);
                        startActivity(intent);
                        finish();
                    }
                }, 300);
            }
        });

    }
}
