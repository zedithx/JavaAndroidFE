package com.example.javaandroidapp;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TestAdd {
    public static String registerUser(FirebaseFirestore db){
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);
        db.collection("user").add(user);
        return "Successful";
    }

}
