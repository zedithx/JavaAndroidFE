package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.utils.ChatSystem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        // create new UserProfile instance
        LinearLayout logoutBtn = findViewById(R.id.logoutBtn);
        ImageButton backBtn = findViewById(R.id.backBtn);

        if (fbUser == null) {
            Intent notSignedIn = new Intent(SettingsActivity.this, LogInActivity.class);
            startActivity(notSignedIn);
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatSystem chatSystem = ChatSystem.getInstance();
                if (chatSystem != null) {
                    chatSystem.client.disconnect(true).enqueue();
                }
                mAuth.signOut();
                Intent logout = new Intent(SettingsActivity.this, LogInActivity.class);
                startActivity(logout);

            }
        });
    }
}
