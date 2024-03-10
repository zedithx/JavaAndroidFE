package com.example.javaandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button loginbutton = (Button)findViewById(R.id.login_button);
        loginbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("Pokemon", "testing123");
                Intent Main = new Intent(LogInActivity.this, LandingActivity.class);
                startActivity(Main);
            }
                                       }
        );
    }

}