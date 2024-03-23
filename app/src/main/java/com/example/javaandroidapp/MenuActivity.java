package com.example.javaandroidapp;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    ImageView profileImageView = findViewById(R.id.profileImageView);
    TextView getUsernameTextView = findViewById(R.id.getUsernameTextView);
    TextView getUserEmailTextView = findViewById(R.id.getUserEmailTextView);
    ImageButton backBtn = findViewById(R.id.backBtn);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_profile_page);

        // create new UserProfile instance
        UserProfile user = new UserProfile();

        // set back button
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // set profile image to display
        profileImageView.setImageResource(user.getProfileImage());

        // set name and email details
        getUsernameTextView.setText(user.getUsername());
        getUserEmailTextView.setText(user.getEmailAddress());

        // get payment methods
    }
}

class UserProfile {
    private String username;
    private String emailAddress;
    private ArrayList<String> paymentMethods = new ArrayList<>();

    private int profileImage;

    UserProfile(){
        username = "Kev Nguyen";
        emailAddress = "kev_nguyen@gmail.com";
        profileImage = R.drawable.profile_pic;
        paymentMethods.add("Stripe");
    }

    public String getUsername(){
        return username;
    }

    public String getEmailAddress(){
        return emailAddress;
    }

    public int getProfileImage(){
        return profileImage;
    }

    public ArrayList getPaymentMethods(){
        return paymentMethods;
    }
}
