package com.example.javaandroidapp.activities;

import static com.example.javaandroidapp.activities.ViewProductActivity.df;
import static com.example.javaandroidapp.activities.ViewProductActivity.listing;

import android.content.Intent;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.Objects;

public class ViewOrderDetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.view_order_details);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        // get order object from previous page
        Order orderDetails = (Order) getIntent().getSerializableExtra("Order");
//        Listing listingDetails = (Listing) getIntent().getSerializableExtra("Listing");

        ImageButton backBtn = findViewById(R.id.backBtn);
        TextView orderName = findViewById(R.id.orderName);
        TextView sellerName = findViewById(R.id.seller);
        TextView variantTextView = findViewById(R.id.order_variant);
        TextView expiryTextView = findViewById(R.id.date);
        ImageView productImage = findViewById(R.id.product_image);
        Log.d("listing log", "" + orderDetails.getListingId());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DocumentReference docRef = db.collection("listings").document(orderDetails.getListingId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        orderName.setText(document.getString("name"));
                        sellerName.setText(document.getString("createdBy"));
                        variantTextView.setText("Variant: " + orderDetails.getVariant());
                        expiryTextView.setText(document.getString("expiryCountdown"));
                        new ImageLoadTask(((ArrayList<String>) document.get("imageList")).get(0), productImage).execute();

                    }
                }
            }
        });



    }
}
