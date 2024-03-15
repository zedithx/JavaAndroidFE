package com.example.javaandroidapp;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Users {
    public static Task<AuthResult> signInUser(FirebaseAuth mAuth, String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public static void registerUser(FirebaseFirestore db, FirebaseUser fbUser) {
        User user = new User();
        db.collection("users").document(fbUser.getUid()).set(user);
        fbUser.sendEmailVerification();
    }

    public static DocumentReference getName(FirebaseFirestore db, FirebaseUser fbUser) {
        return db.collection("users").document(fbUser.getUid());
    }
}
