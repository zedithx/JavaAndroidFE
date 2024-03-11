package com.example.javaandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button loginbutton = (Button)findViewById(R.id.login_button);
        TextView signupLink = (TextView)findViewById(R.id.signup_link);
        TextView forgetLink = (TextView)findViewById(R.id.forget_link);
        loginbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LogInActivity.this, LandingActivity.class);
                startActivity(Main);
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
    }

}