package com.example.javaandroidapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.adapters.CategoryAdapter;
import com.example.javaandroidapp.adapters.ListingAdapter;
import com.example.javaandroidapp.fragments.SearchFragment;
import com.example.javaandroidapp.modals.CategoryModel;
import com.example.javaandroidapp.utils.AlgoliaHelper;
import com.example.javaandroidapp.utils.Categories;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.utils.ChatSystem;
import com.example.javaandroidapp.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.*;

import io.getstream.chat.android.models.User;

public class LandingActivity extends AppCompatActivity {
    private List<CategoryModel> categories = new ArrayList<>();
    private List<Listing> listings = new ArrayList<>();
    boolean pressBackOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // firestore items
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        Users.getUser(db, fbUser, new CallbackAdapter() {
            @Override
            public void getUser(com.example.javaandroidapp.modals.User user_acc) {
                User user = new User.Builder().withId(fbUser.getUid()).withName(user_acc.getName()).withImage(user_acc.getProfileImage()).build();
                ChatSystem chatSystem = ChatSystem.getInstance(getApplicationContext(), user_acc);
            }
        });

        if (fbUser == null) {
            Intent notSignedIn = new Intent(LandingActivity.this, LogInActivity.class);
            startActivity(notSignedIn);
        }
        // set landing page as view
        setContentView(R.layout.landing);
        Intent i = getIntent();
        listings = (List<Listing>) i.getSerializableExtra("listings");
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
        // pull page down to refresh listings
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent main = new Intent(LandingActivity.this, TransitionLandingActivity.class);
                Toast.makeText(LandingActivity.this, "Refreshing Listings", Toast.LENGTH_SHORT).show();
                startActivity(main);
            }
        });
        // double click back button to exit app
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (pressBackOnce){
                    finishAffinity();
                }

                pressBackOnce = true;
                Toast.makeText(LandingActivity.this, "Press back button twice to exit app", Toast.LENGTH_LONG).show();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        pressBackOnce = false;
                    }
                }, 2000);
            }
        });
        // Get the profile button
        LinearLayout profile_button = findViewById(R.id.profile_button);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingActivity.this, MyListingActivity.class);
                Main.putExtra("User", fbUser);
                startActivity(Main);
            }
        });
        // Link for saved button
        LinearLayout saved_button = findViewById(R.id.saved_button);
        saved_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingActivity.this, LandingSavedActivity.class);
                Main.putExtra("User", fbUser);
                startActivity(Main);
            }
        });
        //Link for order button
        LinearLayout order_button = findViewById(R.id.order_button);
        order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingActivity.this, LandingOrdersActivity.class);
                startActivity(Main);
            }
        });

        //Link for chat button
        LinearLayout chat_button = findViewById(R.id.chatbox_button);
        chat_button.setOnClickListener(new View.OnClickListener() {
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


    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    Toast.makeText(this, "You will not receive notifications", Toast.LENGTH_SHORT);
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

}



