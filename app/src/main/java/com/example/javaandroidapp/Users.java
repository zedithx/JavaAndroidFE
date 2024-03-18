package com.example.javaandroidapp;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Users {
    public static void signInUser(FirebaseAuth mAuth, Context context, String email, String password, Callbacks callback) {
        if (email.equals("")){
            Toast.makeText(context, "Error: You entered an empty email", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
        }
        else if (password.equals("")){
            Toast.makeText(context, "Error: You entered an empty password", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        callback.onResult(true);
                    } else {
                        Toast.makeText(context, ErrorHandles.signInErrors(task), Toast.LENGTH_LONG).show();
                        callback.onResult(false);                    }
                }
            });
        }
    }

    public static void registerUser(FirebaseAuth mAuth, FirebaseFirestore db, Context context, String email, String password, String cfmPassword, Callbacks callback) {
        if (email.equals("")){
            Toast.makeText(context, "Error: You entered an empty email", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
        }
        else if (password.equals("")){
            Toast.makeText(context, "Error: You entered an empty password", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
        }
        else if (!(password.equals(cfmPassword))){
            Toast.makeText(context, "Error: The passwords entered do not match", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
        }
        else if (!(email.endsWith("sutd.edu.sg"))) {
            Toast.makeText(context, "Error: This email does not belong to the SUTD organisation", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Please verify your email before signing in!", Toast.LENGTH_SHORT).show();
                        FirebaseUser fbUser = mAuth.getCurrentUser();
                        User user = new User();
                        db.collection("users").document(fbUser.getUid()).set(user);
                        fbUser.sendEmailVerification();
                        callback.onResult(true);
                    } else {
                        Toast.makeText(context, ErrorHandles.signUpErrors(task), Toast.LENGTH_LONG).show();
                        callback.onResult(false);
                    }
                }
            });
        }
    }

    public static void getName(FirebaseFirestore db, FirebaseUser fbUser, Callbacks callback) {
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        callback.getResult(document.getData().get("name").toString());
                    } else {
                        callback.getResult("");
                    }
                }
            }
        });
    }

    public static void getSaved(FirebaseFirestore db, FirebaseUser fbUser, Callbacks callback) {
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<DocumentReference> items = (List<DocumentReference>) document.getData().get("saved");
                    if (items.size() > 0) {
                        Listings.getListings(items, new CallbackAdapter() {
                            @Override
                            public void getList(List<Listing> listings) {
                                if (listings.size() != 0) {
                                    System.out.println("here");
                                    callback.getList(listings);
                                }
                            }
                        });
                    } else {
                        callback.getList(new ArrayList<Listing>());
                    }
                }
            }
        });
    }
}
