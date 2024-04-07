package com.example.javaandroidapp.activities;

import static com.example.javaandroidapp.activities.ViewProductActivity.df;
import static com.example.javaandroidapp.activities.ViewProductActivity.listing;

import android.content.Intent;

import android.graphics.Canvas;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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



        Order orderDetails = (Order) getIntent().getSerializableExtra("Order");
        ImageView processProgress = findViewById(R.id.process_progress);
        RelativeLayout productDetailsLayout = findViewById(R.id.product_details_layout);
        ImageButton backBtn = findViewById(R.id.backBtn);
        MaterialCardView backToMyPageBtn = findViewById(R.id.back_to_my_page_btn);
        TextView orderName = findViewById(R.id.orderName);
        TextView sellerName = findViewById(R.id.seller);
        TextView variantTextView = findViewById(R.id.order_variant);
        TextView expiryTextView = findViewById(R.id.date);
        ImageView productImage = findViewById(R.id.product_image);

        switch (orderDetails.getDelivery()){
            case "Unfulfilled":
                processProgress.setImageResource(R.drawable.process_progress_1);
                break;
            case "Finalised":
                processProgress.setImageResource(R.drawable.progress_process_2);
                break;
            case "Dispatched":
                processProgress.setImageResource(R.drawable.progress_process_3);
                break;
            case "Ready":
                processProgress.setImageResource(R.drawable.progress_process_4);
                break;
            default:
                processProgress.setImageResource(R.drawable.process_progress);
        }
        productDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewProduct = new Intent(ViewOrderDetailsActivity.this, ViewProductActivity.class);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backToMyPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(ViewOrderDetailsActivity.this, TransitionLandingActivity.class);
                startActivity(Main);
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
