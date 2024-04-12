package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.modals.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CollectionActivity extends AppCompatActivity {
    boolean collectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_details);

        Order orderDetails = (Order) getIntent().getSerializableExtra("Order");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        MaterialCardView collectionBtn = findViewById(R.id.collection_btn);
        MaterialCardView backToMyPageBtn = findViewById(R.id.back_to_my_page_btn);
        LinearLayout overlay = findViewById(R.id.overlay);
        ImageButton backBtn = findViewById(R.id.backBtn);
        CardView collectionCard = findViewById(R.id.collection_card);
        TextView orderName = findViewById(R.id.order_name);
        TextView orderVariant = findViewById(R.id.order_variant);
        TextView orderAmount = findViewById(R.id.order_amount);
        ImageView productImage = findViewById(R.id.product_image);
        collectionStatus = orderDetails.getCollectionStatus();
        DocumentReference docRef = db.collection("listings").document(orderDetails.getListingId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        orderName.setText(document.getString("name"));
                        orderVariant.setText("Variant: " + orderDetails.getVariant());
                        orderAmount.setText("Amount: " + orderDetails.getQuantity() );
                        new ImageLoadTask(((ArrayList<String>) document.get("imageList")).get(0), productImage).execute();

                    }
                }
            }
        });

        DocumentReference orderDocRef = db.collection("orders").document(orderDetails.getUid());
        orderDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        collectionStatus = doc.getBoolean("collectionStatus");
                    }
                }
            }
        });
        if (collectionStatus == true){
            overlay.setVisibility(View.VISIBLE);
            collectionBtn.setClickable(false);
            collectionCard.setBackgroundColor(Color.GRAY);
            orderDetails.setCollectionStatus();
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (collectionStatus == false){
            collectionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overlay.setVisibility(View.VISIBLE);
                    collectionBtn.setClickable(false);
                    collectionCard.setBackgroundColor(Color.GRAY);
                    orderDetails.setCollectionStatus();
                    orderDocRef.update("collectionStatus", true);
                }
            });
        }

        backToMyPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(CollectionActivity.this, TransitionLandingActivity.class);
                startActivity(Main);
            }
        });

    }
}
