package com.example.javaandroidapp.utils;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.example.javaandroidapp.modals.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
                        callback.getOrder(orders);
                    }
                }
            });
        }
    }

    //TODO create order
    public static void createOrder(FirebaseFirestore db, String delivery, Listing listing, String paidAmount,
                                   String paymentStatus, Integer quantity, User user, String variant, Callbacks callback) {
        // Create the order here
        // add order to order collection
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
}
