package com.example.javaandroidapp.utils;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.objects.Order;
import com.example.javaandroidapp.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
}
