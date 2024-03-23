package com.example.javaandroidapp.objects;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Order {
    private String delivery;
    private DocumentReference listing;
    private String userid;
    private String paymentStatus;
    private double amount;
    private Date createdDate;

    public Order() {

    }

    public Order(String delivery, DocumentReference listing, String userid, String paymentStatus, double amount, Date createdDate) {
        this.delivery = delivery;
        this.listing = listing;
        this.userid = userid;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.createdDate = createdDate;
    }

    public String getDelivery() {
        return this.delivery;
    }

    public String getUserid() {
        return this.userid;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public double getAmount() {
        return this.amount;
    }

    public DocumentReference getListing() {
        return this.listing;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }
}
