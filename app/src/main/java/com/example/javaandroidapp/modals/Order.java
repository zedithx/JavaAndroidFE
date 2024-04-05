package com.example.javaandroidapp.modals;

import androidx.annotation.NonNull;

import com.example.javaandroidapp.adapters.Callbacks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Order {
    private String delivery;
    private DocumentReference listing;
    private DocumentReference user;
    private String paymentStatus;
    private Integer quantity;
    private String variant;
    private Date createdDate;
    private Double paidAmount;

    public Order() {

    }

    public Order(String delivery, DocumentReference listing, String paymentStatus, Integer quantity, Date createdDate, DocumentReference user, String variant, Double paidAmount) {
        this.delivery = delivery;
        this.listing = listing;
        this.paymentStatus = paymentStatus;
        this.createdDate = createdDate;
        this.user = user;
        this.quantity = quantity;
        this.variant = variant;
        this.paidAmount = paidAmount;
    }

    public String getDelivery() {
        return this.delivery;
    }

    public Double getPaidAmount() {
        return this.paidAmount;
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
