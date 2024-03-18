package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
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
        super.onCreate(savedInstanceState);
        // set page as view
        setContentView(R.layout.landing_saved);
        ImageView back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingSavedActivity.this, LandingActivity.class);
                startActivity(Main);
            }
        });
        // Okay to do this since it embraces singleton design principle
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        // initialise adapters to bind the listings to
        RecyclerView listingRecyclerSavedView = findViewById(R.id.listingRecyclerSavedView);
        LinearLayoutManager listingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listingRecyclerSavedView.setLayoutManager(listingLayoutManager);
        ListingAdapter adapter_listing = new ListingAdapter(listings);
        listingRecyclerSavedView.setAdapter(adapter_listing);
        Users.getSaved(db, fbUser, new CallbackAdapter() {
            @Override
            public void getList(List<Listing> listings_new) {
                if (listings_new.size() != 0) {
                    listings.addAll(listings_new);
                    System.out.println(listings);
                    adapter_listing.notifyDataSetChanged();
                }
            }
        });
    }
}
