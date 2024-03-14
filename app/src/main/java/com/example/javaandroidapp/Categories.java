package com.example.javaandroidapp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Categories {
    public static Query getCategorySnapshot(FirebaseFirestore db) {
        return db.collection("categories").orderBy("name");
    }
}
