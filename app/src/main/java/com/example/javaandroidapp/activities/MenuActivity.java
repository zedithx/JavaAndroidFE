package com.example.javaandroidapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaandroidapp.R;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_profile_page);

        // create new UserProfile instance
        UserProfile user = new UserProfile();

        ImageView profileImageView = findViewById(R.id.profileImageView);
        TextView getUsernameTextView = findViewById(R.id.getUsernameTextView);
        TextView getUserEmailTextView = findViewById(R.id.getUserEmailTextView);
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
        profileImageView.setImageResource(user.getProfileImage());

        // set edit info button
        TextView editInfoBtn = findViewById(R.id.editInfoBtn);

        // set name and email details
        getUsernameTextView.setText(user.getUsername());
        getUserEmailTextView.setText(user.getEmailAddress());

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
//            Intent logout = new Intent(MenuActivity.this, ... );
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
