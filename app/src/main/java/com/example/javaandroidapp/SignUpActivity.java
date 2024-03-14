package com.example.javaandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Signed up");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Users.registerUser(db, user);
                        } else {
                            System.out.println("Failed signed up");
                        }
                    }
                });
            }
        });
    }
}
