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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Orders {
    static int totalOrders = 0;

    public static void getOrdersUser(List<DocumentReference> items, Callbacks callback) {
        List<Order> orders = new ArrayList<>();
        for (DocumentReference item : items) {
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
        order.setUser(userRef);
        order.setListing(listingRef);
        // add order to order collection
        db.collection("orders").add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String orderId = documentReference.getId();
                callback.getResult(orderId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.getResult(null);
            }
        });
    }

    public static void storePaymentId(FirebaseFirestore db, String orderId, String paymentId, Callbacks callback) {
        db.collection("orders").document(orderId).update("paymentId", paymentId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Callback for success
                        callback.getResult(orderId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Callback for failure
                        callback.getResult(null);
                    }
                });
    }

    public static void listingReference(FirebaseFirestore db, String listingUid, String orderId, Callbacks callback) {
        db.collection("listings").document(listingUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot listingRef = task.getResult();
                if (listingRef.exists()) {
                    List<DocumentReference> orders = (List<DocumentReference>) listingRef.get("orders");
                    if (orders == null) {
                        orders = new ArrayList<>();
                    }
                    int quantity = 0;
                    DocumentReference orderReference = db.collection("orders").document(orderId);
                    orders.add(orderReference);
                    Map<String, Object> data = new ConcurrentHashMap<>();
                    data.put("orders", orders);
                    orderReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()){
                                    Listing listing = Listing.createListingWithDocumentSnapshot(listingRef);
                                    data.put("currentOrder", listing.getCurrentOrder() + documentSnapshot.getDouble("quantity").intValue());
                                    db.collection("listings").document(listingUid).update(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Callback for success
                                                    callback.getResult(orderId);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Callback for failure
                                                    callback.getResult(null);
                                                }
                                            });

                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public static void userReference(FirebaseFirestore db, FirebaseUser fbUser, String orderId, Callbacks callback) {
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot userRef = task.getResult();
                if (userRef.exists()) {
                    List<DocumentReference> orders = (List<DocumentReference>) userRef.get("orders");
                    if (orders == null) {
                        orders = new ArrayList<>();
                    }
                    DocumentReference orderReference = db.collection("orders").document(orderId);
                    orders.add(orderReference);
                    db.collection("users").document(fbUser.getUid()).update("orders", orders)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Callback for success
                                    callback.onResult(true);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Callback for failure
                                    callback.onResult(false);
                                }
                            });
                }
            }
        });

    }
}

