package com.example.javaandroidapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.example.javaandroidapp.modals.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.getstream.chat.java.exceptions.StreamException;

public class Users {
    public static void signInUser(FirebaseAuth mAuth, Context context, String email, String password, Callbacks callback) {
        if (email.equals("")) {
            Toast.makeText(context, "Error: You entered an empty email", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
        } else if (password.equals("")) {
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
                        callback.onResult(false);
                    }
                }
            });
        }
    }

    public static void registerUser(FirebaseAuth mAuth, FirebaseFirestore db, Context context, String email, String password, String cfmPassword, Callbacks callback) {
        if (email.equals("")) {
            Toast.makeText(context, "Error: You entered an empty email", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
        } else if (password.equals("")) {
            Toast.makeText(context, "Error: You entered an empty password", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
        } else if (!(password.equals(cfmPassword))) {
            Toast.makeText(context, "Error: The passwords entered do not match", Toast.LENGTH_SHORT).show();
            callback.onResult(false);
        } else if (!(email.endsWith("sutd.edu.sg"))) {
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

    public static void getUser(FirebaseFirestore db, FirebaseUser fbUser, Callbacks callback) {
        db.collection("users").document(fbUser.getUid().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    assert user != null;
                    user.setUserRef(fbUser);
                    try {
                        callback.getUser(user);
                    } catch (StreamException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public static void getUserFromName(FirebaseFirestore db, String sellerName, Callbacks callback) {
        db.collection("users").whereEqualTo("name", sellerName).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    if (document != null) {
                        User user = document.toObject(User.class);
                        try {
                            callback.getUser(user);
                        } catch (StreamException e) {
                            throw new RuntimeException(e);
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
                    if (items != null) {
                        Listings.getSavedListings(items, new CallbackAdapter() {
                            @Override
                            public void getList(List<Listing> listings) {
                                if (listings.size() != 0) {
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
    public static void getOrder(FirebaseFirestore db, FirebaseUser fbUser, Callbacks callback) {
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<DocumentReference> items = (List<DocumentReference>) document.getData().get("orders");
                    if (items != null) {
                        Orders.getOrdersUser(items, new CallbackAdapter() {
                            @Override
                            public void getOrders(List<Order> orders) {
                                if (orders.size() != 0) {
                                    callback.getOrders(orders);
                                }
                            }
                        });
                    } else {
                        callback.getOrders(new ArrayList<Order>());
                    }
                }
            }
        });
    }
    public static void getListingCreated(FirebaseFirestore db, FirebaseUser fbUser, Callbacks callback) {
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<DocumentReference> items = (List<DocumentReference>) document.getData().get("listings");
                    if (items != null) {
                        Listings.getMerchantListings(items, new CallbackAdapter() {
                            @Override
                            public void getList(List<Listing> listings) {
                                if (listings.size() != 0) {
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
    public static void isSaved(FirebaseFirestore db, FirebaseUser fbUser, Listing listing, Callbacks callback) {
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot userRef = task.getResult();
                if (userRef.exists()) {
                    List<DocumentReference> saved = (List<DocumentReference>) userRef.get("saved");
                    DocumentReference listingReference = db.collection("listings").document(listing.getUid());
                    // Check if listingReference is in saved, then remove
                    if (saved == null) {
                        callback.onResult(false);
                        return;
                    }
                    else{
                        for (DocumentReference ref : saved) {
                            if (ref.getPath().equals(listingReference.getPath())) {
                                callback.onResult(true);
                                return;
                            }
                        }
                    }
                    callback.onResult(false);
                }
            }
        });
    }
    public static void saveListing(FirebaseFirestore db, FirebaseUser fbUser, Listing listing, Callbacks callback) {
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot userRef = task.getResult();
                if (userRef.exists()) {
                    List<DocumentReference> saved = (List<DocumentReference>) userRef.get("saved");
                    if (saved == null) {
                        saved = new ArrayList<>();
                    }
                    DocumentReference listingReference = db.collection("listings").document(listing.getUid());
                    saved.add(listingReference);
                    db.collection("users").document(fbUser.getUid()).update("saved", saved)
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
    public static void removeSavedListing(FirebaseFirestore db, FirebaseUser fbUser, Listing listing, Callbacks callback) {
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot userRef = task.getResult();
                if (userRef.exists()) {
                    List<DocumentReference> saved = (List<DocumentReference>) userRef.get("saved");
                    DocumentReference listingReference = db.collection("listings").document(listing.getUid());
                    // Check if listingReference is in saved, then remove
                    Iterator<DocumentReference> iterator = saved.iterator();
                    while (iterator.hasNext()) {
                        DocumentReference ref = iterator.next();
                        if (ref.getPath().equals(listingReference.getPath())) {
                            // Remove the element using the iterator's remove method
                            iterator.remove();
                            break;
                        }
                    }
                    db.collection("users").document(fbUser.getUid()).update("saved", saved)
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

