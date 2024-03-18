package com.example.javaandroidapp.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.objects.Listing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Listings {
    public static void getAllListings(FirebaseFirestore db, String category, Callbacks callbacks) {
        Query item;
        Log.d("listings", "retrieved" + category);
        if (category.equals("All")) {
            item =  db.collection("Listings");
        } else if (category.equals("Popular")) {
            item = db.collection("Listings").orderBy("currentorder");
        } else {
            item = db.collection("Listings").whereEqualTo("category", category);
        }

        item.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Listing> listings = new ArrayList<>();
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        String listing_price = document.get("price").toString();
                        String listing_name = document.getString("name");
                        String listing_minorder = document.get("minorder").toString();
                        String listing_currentorder = document.get("currentorder").toString();
                        String listing_image = document.get("image").toString();
                        Date listing_expirydate = document.getDate("expiry");
                        Listing listing = new Listing(listing_price, listing_name, listing_minorder,
                                listing_currentorder, listing_expirydate, listing_image);
                        listings.add(listing);
                    }
                    callbacks.getList(listings);
                }
            }
        });
    }

    public static void getListings(List<DocumentReference> items, Callbacks callback) {
        List<Listing> listings = new ArrayList<>();
        for (DocumentReference item: items) {
            item.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        String listing_price = document.get("price").toString();
                        String listing_name = document.getString("name");
                        String listing_minorder = document.get("minorder").toString();
                        String listing_currentorder = document.get("currentorder").toString();
                        String listing_image = document.get("image").toString();
                        Date listing_expirydate = document.getDate("expiry");
                        Listing listing = new Listing(listing_price, listing_name, listing_minorder,
                                listing_currentorder, listing_expirydate, listing_image);
                        listings.add(listing);
                        callback.getList(listings);
                    }
                }
            });
        }
    }
}
