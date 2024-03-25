package com.example.javaandroidapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.objects.User;
import com.example.javaandroidapp.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_profile_page);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        // create new UserProfile instance

        if (fbUser == null) {
            Intent notSignedIn = new Intent(MenuActivity.this, LogInActivity.class);
            startActivity(notSignedIn);
        }

        ImageView profileImageView = findViewById(R.id.profileImageView);
        TextView getUsernameTextView = findViewById(R.id.getUsernameTextView);
        TextView getUserEmailTextView = findViewById(R.id.getUserEmailTextView);
        Users.getUser(db, fbUser, new CallbackAdapter() {
            @Override
            public void getUser(User user_new) {
                user = user_new;
                if (user.getProfileImage().equals("")) {
                    Glide.with(profileImageView).load(R.drawable.profile_pic).into(profileImageView);
                } else {
                    Glide.with(profileImageView).load(user.getProfileImage()).into(profileImageView);
                }
                getUsernameTextView.setText(user.getName());
                getUserEmailTextView.setText(user.getUserRef().getEmail().toString());
            }
        });


        ImageButton backBtn = findViewById(R.id.backBtn);

        // set back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(MenuActivity.this, LandingActivity.class);
                startActivity(Main);
            }
        });

        // set profile image to display

        // set edit info button
        TextView editInfoBtn = findViewById(R.id.editInfoBtn);

        // set name and email details
        // set add/view listing buttons
        CardView addListing = findViewById(R.id.addListingCard);
        CardView viewListing = findViewById(R.id.viewListingCard);
        viewListing.setBackgroundResource(R.drawable.dotted);
        addListing.setBackgroundResource(R.drawable.dotted);

        // set payment/logout buttons
        TextView paymentMethodBtn = findViewById(R.id.paymentMethodsBtn);
        TextView logoutBtn = findViewById(R.id.logoutBtn);

        editInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent editInfo = new Intent(MenuActivity.this, EditInfoActivity.class);
            }
        });

        viewListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent viewListingsIntent = new Intent(MenuActivity.this, ViewListing.class);
            }
        });
        addListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(MenuActivity.this, AddListingActivity.class);
                Main.putExtra("User", fbUser);
                startActivity(Main);
//                Intent addListingsIntent = new Intent(MenuActivity.this, AddListing.class);
            }
        });
        paymentMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent paymentMethods = new Intent(MenuActivity.this, PaymentMethodActivity.class);
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent logout = new Intent(MenuActivity.this, LogInActivity.class);
                startActivity(logout);

            }
        });


    }
}

class UserProfile {
    private String username;
    private String emailAddress;
    private ArrayList<String> paymentMethods = new ArrayList<>();

    private int profileImage;

    UserProfile() {
        username = "Kev Nguyen";
        emailAddress = "kev_nguyen@gmail.com";
        profileImage = R.drawable.profile_pic;
        paymentMethods.add("Stripe");
    }

    public String getUsername() {
        return username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public ArrayList getPaymentMethods() {
        return paymentMethods;
    }
}
