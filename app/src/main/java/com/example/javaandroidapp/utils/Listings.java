package com.example.javaandroidapp.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.objects.Listing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
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
            item = db.collection("Listings").orderBy("currentOrder");
        } else {
            item = db.collection("Listings").whereEqualTo("category", category);
        }

        item.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Listing> listings = task.getResult().toObjects(Listing.class);
                    callbacks.getList(listings);
                }
            }
        });
    }

    public static void getSavedListings(List<DocumentReference> items, Callbacks callback) {
        List<Listing> listings = new ArrayList<>();
        for (DocumentReference item: items) {
            item.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Listing listing = document.toObject(Listing.class);
                        listings.add(listing);
                        callback.getList(listings);
                    }
                }
            });
        }
    }
    public static void addListing(FirebaseFirestore db, FirebaseUser fbUser, Double price, String name, Integer minOrder, Integer currentOrder, Date expiryDate,
                                  ArrayList<String> imageList, String description, Double oldPrice, String category, ArrayList<String> variationNames,
                                  ArrayList<Double> variationAdditionalPrice, Callbacks callback) {
        Listing listing = new Listing(price, name, minOrder, currentOrder, expiryDate,
                imageList, fbUser.getEmail(), description, oldPrice, category, variationNames, variationAdditionalPrice);
        db.collection("Listings").add(listing).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                callback.onResult(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onResult(false);
            }
        });
    }
}
