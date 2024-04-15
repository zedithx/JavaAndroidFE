package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.utils.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class MerchantDepositActivity extends AppCompatActivity {
    String deliveryStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_deposit);

        Listing listingDetails = (Listing) getIntent().getSerializableExtra("Listing");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        MaterialCardView depositBtn = findViewById(R.id.deposit_btn);
        MaterialCardView backToMyPageBtn = findViewById(R.id.back_to_my_page_btn);
        LinearLayout overlay = findViewById(R.id.overlay);
        ImageButton backBtn = findViewById(R.id.backBtn);
        CardView depositCard = findViewById(R.id.deposit_card);
        TextView listingName = findViewById(R.id.listingName);
        TextView expiryText = findViewById(R.id.expiryDate);
        ImageView listingImage = findViewById(R.id.listingImage);
        TextView currentOrder = findViewById(R.id.currentOrder);
        TextView minOrder = findViewById(R.id.minOrder);

        ImageView qrcode = findViewById(R.id.qr_code);
        //add attribute and change to depositedStatus
        deliveryStatus = listingDetails.getDeliveryStatus();
        DocumentReference docRef = db.collection("listings").document(listingDetails.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // set all the textviews etc
                        Listing listing = document.toObject(Listing.class);
                        listingName.setText(listing.getName());
                        String expiryCountdown = listing.getExpiryCountdown();
                        Date expiryDate = listing.getExpiry();
                        if (expiryDate.after(new Date())) {
                            expiryText.setText(expiryCountdown);
                        } else {
                            expiryText.setText("Expired");
                        }
                        currentOrder.setText(listing.getCurrentOrder().toString());
                        minOrder.setText(listing.getMinOrder().toString());
                        new ImageLoadTask(((ArrayList<String>) listing.getImageList()).get(0), listingImage).execute();
                        // get collection status and set qrcode
                        deliveryStatus = listing.getDeliveryStatus();
                        qrcode.setImageBitmap(QRCode.createQR(listingDetails.getUid()));
                    }
                }
            }
        });

        if (deliveryStatus.equals("Ready")){
            overlay.setVisibility(View.VISIBLE);
            depositBtn.setClickable(false);
            depositCard.setBackgroundColor(Color.GRAY);
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (deliveryStatus.equals("Dispatched")){
            depositBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overlay.setVisibility(View.VISIBLE);
                    depositBtn.setClickable(false);
                    depositCard.setBackgroundColor(Color.GRAY);
                    listingDetails.setDeliveryStatus("Ready");
                    docRef.update("deliveryStatus", "Ready");

                }
            });
        }

        backToMyPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(MerchantDepositActivity.this, ManageOrderActivity.class);
                startActivity(Main);
            }
        });

    }
}
