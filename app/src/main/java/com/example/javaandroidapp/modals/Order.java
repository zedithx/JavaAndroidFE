package com.example.javaandroidapp.modals;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.adapters.Callbacks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    @DocumentId
    private String uid;
    private String delivery;
    private transient DocumentReference listing;
    private transient DocumentReference user;
    private String paymentStatus;
    private Integer quantity;
    private String variant;
    private Date createdDate;
    private Double paidAmount;

    public Order() {

    }
    public String getUid() {
        return uid;
    }

    public DocumentReference getListing() {
        return listing;
    }

    public void setListing(DocumentReference listing) {
        this.listing = listing;
    }

    public void setUser(DocumentReference user) {
        this.user = user;
    }

    public Order(Integer quantity,
                 Date createdDate, String variant, Double paidAmount) {
        this.delivery = "Unfulfilled";
        this.paymentStatus = "Not Paid";
        this.createdDate = createdDate;
        this.quantity = quantity;
        this.variant = variant;
        this.paidAmount = paidAmount;
    }

    public String getDelivery() {
        return this.delivery;
    }

    public Double getPaidAmount() {
        return Double.valueOf(String.format("%.2f", this.paidAmount));
    }

    public DocumentReference getUser() {
        return this.user;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public String getVariant() {
        return this.variant;
    }

    public void getListing(Callbacks callback) {
        if (this.listing != null) {
            this.listing.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Listing listing = document.toObject(Listing.class);
                        callback.getOrderList(listing);
                    } else {
                        callback.getOrderList(null);
                    }

                }

            });
        }
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }
}
