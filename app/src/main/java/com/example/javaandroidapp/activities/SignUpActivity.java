package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        ImageView backButton = findViewById(R.id.signup_back);
        Button signUpButton = findViewById(R.id.signup_button);
        EditText signUpEmail = findViewById(R.id.signupEmail);
        EditText signUpPassword = findViewById(R.id.signUpPassword);
        EditText signUpCfmPassword = findViewById(R.id.signUpCfmPassword);
//        LayoutInflater inflater = getLayoutInflater();
//        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.toast_signup));

// Customize the ImageView and TextView in the layout if needed
        backButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               Intent Main = new Intent(SignUpActivity.this, LogInActivity.class);
               startActivity(Main);
           }
       });
        signUpButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = signUpEmail.getText().toString();
                String password = signUpPassword.getText().toString();
                String cfmPassword = signUpCfmPassword.getText().toString();
                Users.registerUser(mAuth, db, SignUpActivity.this, email, password, cfmPassword, new CallbackAdapter() {
                    @Override
                    public void onResult(boolean isSuccess) {
                        if (isSuccess) {
                            Intent main = new Intent(SignUpActivity.this, LogInActivity.class);
                            startActivity(main);
                        }
                    }
                });
            }
        });
    }
}
