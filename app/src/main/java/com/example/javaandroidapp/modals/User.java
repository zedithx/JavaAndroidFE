package com.example.javaandroidapp.modals;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class User {
    @DocumentId
    private String uid;
    private String name;
    private List<DocumentReference> orders;
    private List<DocumentReference> saved;
    private List<DocumentReference> listings;
    private FirebaseUser userRef;
    private String profileimage;
    private List<String> paymentMethods;
    private String userIdToken;

    public User(){
        this.name = "Default user";
    }

    public User(String userIdToken) {
        this.name = "Default user";
        this.userIdToken = userIdToken;
    }

    public User(String name, String userIdToken) {
        this.name = name;
        //TODO - add profile pic
        this.saved  = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.listings = new ArrayList<>();
        this.userIdToken = userIdToken;
    }

    public String getUid() {
        return uid;
    }

    public List<DocumentReference> getOrders() {
        return this.orders;
    }

    public void setOrders(List<DocumentReference> orders) {
        this.orders = orders;
    }

    public void setSaved(List<DocumentReference> saved) {
        this.saved = saved;
    }

    public List<DocumentReference> getSaved() {
        return this.orders;
    }

    public void setUserRef(FirebaseUser userRef) {
        this.userRef = userRef;
    }

    public FirebaseUser getUserRef() {
        return this.userRef;
    }


    public String getName() { return this.name; }

    public String getProfileImage() {return this.profileimage; }

    public void setProfileImage(String profileImage) {
        this.profileimage = profileImage;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getUserIdToken() {
        return userIdToken;
    }

    public void setUserIdToken(String userIdToken) {
        this.userIdToken = userIdToken;
    }
}
