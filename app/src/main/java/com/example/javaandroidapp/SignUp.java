package com.example.javaandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        Button loginbutton = (Button)findViewById(R.id.login_button);
        loginbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("Pokemon", "testing123");
                Intent logIn = new Intent(SignUp.this, Main.class);
                startActivity(logIn);
            }
                                       }
        );
    }

}