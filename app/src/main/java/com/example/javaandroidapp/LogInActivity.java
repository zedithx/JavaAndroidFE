package com.example.javaandroidapp;

import java.util.*;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button loginbutton = findViewById(R.id.login_button);
        TextView signupLink = findViewById(R.id.signup_link);
        TextView forgetLink = (TextView)findViewById(R.id.forget_link);
        loginbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LogInActivity.this, LandingActivity.class);
                startActivity(Main);
                TestAdd.registerUser(db);
            }
                                       }
        );
        signupLink.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               Intent Main = new Intent(LogInActivity.this, SignUpActivity.class);
               startActivity(Main);
           }
       }
        );
        forgetLink.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(View v) {
                  Intent Main = new Intent(LogInActivity.this, ForgotPassActivity.class);
                  startActivity(Main);
              }
          }
        );
    }

}