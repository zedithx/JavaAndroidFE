package com.example.javaandroidapp.utils;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.objects.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Orders {
    public static void getOrdersBasedOnUser(FirebaseFirestore db, FirebaseUser fbUser, Callbacks callback) {
        db.collection("orders").whereEqualTo("user", fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot query = task.getResult();
                if (task.isSuccessful()) {
                    List<Order> userOrders = query.toObjects(Order.class);
                    callback.getOrder(userOrders);
                } else {
                    callback.getOrder(new ArrayList<Order>());
                }
            }
        });
    }

}
