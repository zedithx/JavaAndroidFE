package com.example.javaandroidapp;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Users {
    public static Task<AuthResult> registerUser(FirebaseAuth mAuth, String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public static Task<AuthResult> signInUser(FirebaseAuth mAuth, String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public static String registerUser1(FirebaseFirestore db, String userID, int phone_no, String address) {
        User user = new User(phone_no, address);
        db.collection("users").document(userID).set(user);
        return "String";
    }

}
