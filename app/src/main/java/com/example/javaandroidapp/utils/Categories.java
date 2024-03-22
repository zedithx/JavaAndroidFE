package com.example.javaandroidapp.utils;

import android.telecom.Call;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.activities.LandingActivity;
import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.objects.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Categories {
    public static void getCategorySnapshot(FirebaseFirestore db, Callbacks callback) {
        db.collection("categories").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<CategoryModel> categories = new ArrayList<>();
                    categories.add(new CategoryModel("All", true));
                    categories.add(new CategoryModel("Popular", false));
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        categories.add(new CategoryModel(document.getData().get("name").toString(), false));
                    }
                    callback.getCategory(categories);
                }
            }
        });
    }

    public static void getCategoryString(FirebaseFirestore db, Callbacks callback) {
        db.collection("categories").orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> categories = new ArrayList<>();
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        categories.add(document.getData().get("name").toString());
                    }
                    callback.getListOfString(categories);
                }
            }
        });
    }
}
