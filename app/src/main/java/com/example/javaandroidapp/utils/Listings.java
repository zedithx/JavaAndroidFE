package com.example.javaandroidapp.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.example.javaandroidapp.modals.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Listings {
    public static void getAllListings(FirebaseFirestore db, String category, Callbacks callbacks) {
        Query item;
        Date date = new Date();
        if (category.equals("All")) {
            item = db.collection("listings").whereGreaterThanOrEqualTo("expiry", date).orderBy("expiry", Query.Direction.ASCENDING);
        } else if (category.equals("Popular")) {
            item = db.collection("listings").whereGreaterThanOrEqualTo("expiry", date).orderBy("currentOrder", Query.Direction.DESCENDING);
        } else {
            item = db.collection("listings").whereEqualTo("category", category).whereGreaterThan("expiry", date).orderBy("expiry", Query.Direction.ASCENDING);
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
    public static void addListing(FirebaseFirestore db, User user, Double price, String name, Integer minOrder, Date expiryDate,
                                  ArrayList<String> imageList, String description, Double oldPrice, String category, ArrayList<String> variationNames,
                                  ArrayList<Double> variationAdditionalPrice, Callbacks callback) {
        Listing listing = new Listing(price, name, minOrder, expiryDate,
                imageList, user.getName(), user.getUid(), description, oldPrice, category, variationNames, variationAdditionalPrice);
        db.collection("listings").add(listing).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

    public static void getMerchantListings(List<DocumentReference> items, Callbacks callback) {
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

    public static void searchListing(FirebaseFirestore db, List<String> doc, Callbacks callback) {
        Date date = new Date();
        List<Listing> listings = new ArrayList<>();
        for (String document : doc) {
            db.collection("listings").document(document).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Listing listing = task.getResult().toObject(Listing.class);
                        listings.add(listing);
                        callback.getList(listings);
                    }
                }
            });
        }
    }
}
