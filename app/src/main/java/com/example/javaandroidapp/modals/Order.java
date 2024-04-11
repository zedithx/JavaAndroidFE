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
    private String listingId;
    private String delivery;
    private boolean collectionStatus;
    private transient DocumentReference listing;
    private transient DocumentReference user;
    private String paymentStatus;
    private Integer quantity;
    private String variant;
    private Date createdDate;
    private Double itemPrice;
    private Double paidAmount;

    public Order() {

    }

    public String getUid() {
        return uid;
    }

    public String getListingId() {
        return listingId;
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

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Order(String listingId, Integer quantity,
                 Date createdDate, String variant, Double itemPrice, Double paidAmount) {
        this.listingId = listingId;
        this.delivery = "Unfulfilled";
        this.paymentStatus = "Not Paid";
        this.createdDate = createdDate;
        this.quantity = quantity;
        this.variant = variant;
        this.itemPrice = itemPrice;
        this.paidAmount = paidAmount;
        this.collectionStatus = false;
    }

    public String getDelivery() {
        return this.delivery;
    }

    public void setCollectionStatus() {
        collectionStatus = true;
    }
    public boolean getCollectionStatus() {
        return collectionStatus;
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
