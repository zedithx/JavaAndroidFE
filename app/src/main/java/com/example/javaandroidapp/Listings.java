package com.example.javaandroidapp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Listings {
    public static Query getAllListings(FirebaseFirestore db, String category) {
        if (category.equals("All")) {
            return db.collection("Listings");
        } else if (category.equals("Popular")) {
            return db.collection("Listings").orderBy("currentorder");
        } else {
            return db.collection("Listings").whereEqualTo("category", category);
        }
    }
}
