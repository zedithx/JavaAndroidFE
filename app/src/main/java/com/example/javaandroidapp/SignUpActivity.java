package com.example.javaandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
                if (email.equals("")){
//                    TextView textView = layout.findViewById(R.id.toast_content);
//                    textView.setText("test");
//
//                    Toast toast = new Toast(getApplicationContext());
//                    toast.setDuration(Toast.LENGTH_SHORT);
//                    toast.setView(layout);
//                    toast.show();
                    Toast.makeText(SignUpActivity.this, "Error: You entered an empty email", Toast.LENGTH_SHORT).show();
                }
                else if (password.equals("")){
                    Toast.makeText(SignUpActivity.this, "Error: You entered an empty password", Toast.LENGTH_SHORT).show();
                }
                else if (!(password.equals(cfmPassword))){
                    Toast.makeText(SignUpActivity.this, "Error: The passwords entered do not match", Toast.LENGTH_SHORT).show();
                }
                else if (!(email.endsWith("sutd.edu.sg"))) {
                    Toast.makeText(SignUpActivity.this, "Error: This email does not belong to the SUTD organisation", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Please verify your email before signing in!", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                Users.registerUser(db, user);
                                Intent Main = new Intent(SignUpActivity.this, LogInActivity.class);
                                startActivity(Main);
                            } else {
                                Toast.makeText(SignUpActivity.this, ErrorHandles.signUpErrors(task), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
