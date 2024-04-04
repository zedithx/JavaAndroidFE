package com.example.javaandroidapp.modals;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private Integer phoneNumber;
    private String address;
    private List<DocumentReference> orders;
    private List<DocumentReference> saved;
    private List<DocumentReference> listings;
    private FirebaseUser userRef;
    private String profileimage;
    private List<String> paymentMethods;


    public User() {
        this.phoneNumber = 0;
        this.name = "Default user";
        this.address = "";
    }

    public User(int phoneNumber, String address, String name) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.saved  = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.listings = new ArrayList<>();
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

    public int getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() { return this.name; }

    public String getProfileImage() {return this.profileimage; }

    public void setProfileImage(String profileImage) {
        this.profileimage = profileImage;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

}
