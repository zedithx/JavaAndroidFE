package com.example.javaandroidapp;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Users {
    public static Task<AuthResult> registerUser(FirebaseAuth mAuth, String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public static Task<AuthResult> signInUser(FirebaseAuth mAuth, String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

}
