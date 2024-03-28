package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.adapters.CategoryAdapter;
import com.example.javaandroidapp.adapters.ListingAdapter;
import com.example.javaandroidapp.fragments.SearchFragment;
import com.example.javaandroidapp.objects.CategoryModel;
import com.example.javaandroidapp.utils.AlgoliaHelper;
import com.example.javaandroidapp.utils.Categories;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.utils.Listings;
import com.example.javaandroidapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.*;

public class LandingActivity extends AppCompatActivity {
    private List<CategoryModel> categories = new ArrayList<>();
    private List<Listing> listings = new ArrayList<>();

    private QuerySnapshot listing_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // firestore items
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        if (fbUser == null) {
            Intent notSignedIn = new Intent(LandingActivity.this, LogInActivity.class);
            startActivity(notSignedIn);
        }
        // set landing page as view
        setContentView(R.layout.landing);
        // category horizontal carousel
        RecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);
        // Listing vertical carousel
        RecyclerView listingRecyclerView = findViewById(R.id.listingRecyclerView);
        LinearLayoutManager listingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listingRecyclerView.setLayoutManager(listingLayoutManager);
        // Get name view to edit
//        TextView username = findViewById(R.id.username);
        // Retrieve user's name
//        Users.getName(db, fbUser, new CallbackAdapter() {
//            @Override
//            public void getResult(String result) {
//                if (!result.equals("")) {
//                    username.setText(String.format("Hi, %s!", result));
//                } else {
//                    username.setText("Hi, User!");
//                }
//            }
//        });
        // Get the search button
        ImageView search_button = findViewById(R.id.search);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                        .replace(R.id.listingsFrameLayout, new SearchFragment())
                        .addToBackStack(null)
                        .commit();
            }

        });
        // Get the profile button
        ImageView profile_button = findViewById(R.id.avatar);

        AlgoliaHelper.searchListingID("minecraft tutorial", new CallbackAdapter() {
            @Override
            public void getList(List<Listing> item) {
                System.out.println(item);
            }
        });
        profile_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingActivity.this, MenuActivity.class);
                Main.putExtra("User", fbUser);
                startActivity(Main);
            }
        });
        // Link for saved button
        LinearLayout saved_button = findViewById(R.id.saved_button);
        saved_button.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                 Intent Main = new Intent(LandingActivity.this, LandingSavedActivity.class);
                 Main.putExtra("User", fbUser);
                 startActivity(Main);
             }
        });
        //Link for order button
        LinearLayout order_button = findViewById(R.id.order_button);
        order_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingActivity.this, LandingOrdersActivity.class);
                startActivity(Main);
            }
        });

        //Link for chat button
        ImageView chat_button = findViewById(R.id.chatbox);
        chat_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingActivity.this, ChannelActivity.class);
                startActivity(Main);
            }
        });
        ListingAdapter adapter_listing = new ListingAdapter(listings);
        adapter_listing.setOnItemClickListener(new ListingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Listing data) {
                // Handle item click, e.g., start a new activity
                Intent intent = new Intent(LandingActivity.this, TransitionViewProductActivity.class);
                intent.putExtra("listing", data);
                startActivity(intent);
            }
        });
        listingRecyclerView.setAdapter(adapter_listing);
        // Retrieve all listings
        Listings.getAllListings(db, "All", new CallbackAdapter() {
            @Override
            public void getList(List<Listing> listings_new) {
                if (listings_new.size() != 0) {
                    listings.addAll(listings_new);
                }
                adapter_listing.notifyDataSetChanged();
            }
        });

        CategoryAdapter adapter = new CategoryAdapter(categories, listings, db, adapter_listing);
        Categories.getCategorySnapshot(db, new CallbackAdapter() {
            @Override
            public void getCategory(List<CategoryModel> categories_new) {
                if (categories_new.size() != 0) {
                    categories.addAll(categories_new);
                    categoryRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

}


