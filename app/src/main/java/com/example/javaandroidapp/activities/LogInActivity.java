package com.example.javaandroidapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button loginbutton = findViewById(R.id.login_button);
        TextView signupLink = findViewById(R.id.signup_link);
        TextView forgetLink = (TextView)findViewById(R.id.forget_link);
        EditText loginEmail = findViewById(R.id.loginEmail);
        EditText loginPassword = findViewById(R.id.loginPassword);
        loginbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                Users.signInUser(mAuth, LogInActivity.this, email, password, new CallbackAdapter() {
                    @Override
                    public void onResult(boolean isSuccess) {
                        if (isSuccess) {
                            Intent Main = new Intent(LogInActivity.this, SplashScreen.class);
                            startActivity(Main);
                        }
                    }
                });
            }
        });
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