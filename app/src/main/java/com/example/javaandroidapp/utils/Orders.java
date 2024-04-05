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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Orders {
    public static void getOrdersUser(List<DocumentReference> items, Callbacks callback) {
        List<Order> orders = new ArrayList<>();
        for (DocumentReference item: items) {
            item.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Order order = document.toObject(Order.class);
                        orders.add(order);
                        callback.getOrders(orders);
                    }
                }
            });
        }
    }

    //TODO create order
    public static void createOrder(FirebaseFirestore db, Order order, FirebaseUser fbUser, String listingUid, Callbacks callback) {
        DocumentReference userRef = db.collection("users").document(fbUser.getUid());
        DocumentReference listingRef = db.collection("listings").document(listingUid);
        Log.d("listingUid", "listingUid: " + listingUid);
        Log.d("userRef", "userRef: " + userRef);
        Log.d("listingRef", "userRef: " + listingRef);
        order.setUser(userRef);
        order.setListing(listingRef);
        // add order to order collection
        db.collection("orders").add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
    public static void storeClientSecret(FirebaseFirestore db, Order order, String ClientSecret, Callbacks callback) {
        db.collection("orders").document(order.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot orderRef = task.getResult();
                if (orderRef != null){
                    db.collection("orders").document(order.getUid()).update("clientSecret", ClientSecret);
                }
            }
        });
    }
}

