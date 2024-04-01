package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.adapters.ListingAdapter;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LandingSavedActivity  extends AppCompatActivity {
    private List<Listing> listings = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        if (fbUser == null) {
            Intent notSignedIn = new Intent(LandingSavedActivity.this, LogInActivity.class);
            startActivity(notSignedIn);
        }
        super.onCreate(savedInstanceState);
        // set page as view
        setContentView(R.layout.landing_saved);
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingSavedActivity.this, TransitionLandingActivity.class);
                startActivity(Main);
            }
        });
        // Okay to do this since it embraces singleton design principle
        // initialise adapters to bind the listings to
        RecyclerView listingRecyclerSavedView = findViewById(R.id.listingRecyclerSavedView);
        LinearLayoutManager listingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listingRecyclerSavedView.setLayoutManager(listingLayoutManager);
        ListingAdapter adapter_listing = new ListingAdapter(listings);
        listingRecyclerSavedView.setAdapter(adapter_listing);
        Users.getSaved(db, fbUser, new CallbackAdapter() {
            @Override
            public void getList(List<Listing> listings_new) {
                listings.clear();
                if (listings_new.size() != 0) {
                    listings.addAll(listings_new);
                    adapter_listing.notifyDataSetChanged();
                }
            }
        });
        adapter_listing.setOnItemClickListener(new ListingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Listing data) {
                // Handle item click, e.g., start a new activity
                Intent intent = new Intent(LandingSavedActivity.this, ViewProductActivity.class);
                intent.putExtra("listing", data);
                startActivity(intent);
            }
        });
    }
}
