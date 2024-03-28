package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.adapters.ListingAdapter;
import com.example.javaandroidapp.adapters.OrderAdapter;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.objects.Order;
import com.example.javaandroidapp.objects.User;
import com.example.javaandroidapp.utils.Listings;
import com.example.javaandroidapp.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private List<Listing> searchResults = new ArrayList<>();
    private List<String> listings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set page as view
        setContentView(R.layout.search_listing);
        listings = getIntent().getStringArrayListExtra("listings");
        TextView header_name = findViewById(R.id.header_saved);
        header_name.setText("Search");
        TextView title_name = findViewById(R.id.title_saved);
        title_name.setText("Search Results");
        ImageView back_arrow = findViewById(R.id.back_arrow);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        // initialise adapters to bind the listings to
        RecyclerView listingRecyclerSearchView = findViewById(R.id.listingRecyclerSearchView);
        LinearLayoutManager listingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listingRecyclerSearchView.setLayoutManager(listingLayoutManager);
        ListingAdapter search_adapter = new ListingAdapter(searchResults);
        listingRecyclerSearchView.setAdapter(search_adapter);


        if (fbUser == null) {
            Intent notSignedIn = new Intent(SearchActivity.this, LogInActivity.class);
            startActivity(notSignedIn);
        }
        if (listings != null) {
            Listings.searchListing(db, listings, new CallbackAdapter(){
                @Override
                public void getList(List<Listing> listing_queries){
                    searchResults.addAll(listing_queries);
                    search_adapter.notifyDataSetChanged();
                }
            });
        }
        back_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(SearchActivity.this, LandingActivity.class);
                startActivity(Main);
            }
        });
        search_adapter.setOnItemClickListener(new ListingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Listing data) {
                // Handle item click, e.g., start a new activity
                Intent intent = new Intent(SearchActivity.this, ViewProductActivity.class);
                intent.putExtra("listing", data);
                startActivity(intent);
            }
        });
    }
}
