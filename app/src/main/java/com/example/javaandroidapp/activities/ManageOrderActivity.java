package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.modals.Listing;
import com.google.android.material.card.MaterialCardView;

public class ManageOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_order);

        Listing listing = (Listing) getIntent().getSerializableExtra("listing");

        ImageButton backBtn = findViewById(R.id.backBtn);
        TextView listingName = findViewById(R.id.listingName);
        TextView orderFulfilledText = findViewById(R.id.orderFulfilledText);
        MaterialCardView orderFulfilledCardMat = findViewById(R.id.orderFulfilledCard);
        ImageView fulfilImage = findViewById(R.id.fulfilImage);
        TextView currentOrder = findViewById(R.id.currentOrder);
        TextView minOrder = findViewById(R.id.minOrder);
        TextView expiryText = findViewById(R.id.expiryDate);
        ImageView productImg = findViewById(R.id.listingImage);
        LinearLayout optionsLayout = findViewById(R.id.optionsLayout);
        LinearLayout expiryLayout = findViewById(R.id.expiryLayout);
        boolean minFulfilled = false;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile = new Intent(ManageOrderActivity.this, MyListingActivity.class);
                startActivity(toProfile);
            }
        });

        listingName.setText(listing.getName());
        int minOrderNum = listing.getMinOrder().intValue();
        int currOrderNum = listing.getCurrentOrder().intValue();
        minOrder.setText("" + minOrderNum);
        currentOrder.setText("" + currOrderNum);
        String expiryCountdown = listing.getExpiryCountdown();
        expiryText.setText(expiryCountdown);
        new ImageLoadTask(listing.getImageList().get(0), productImg).execute();

        if (currOrderNum >= minOrderNum){
            minFulfilled = true;
            fulfilImage.setImageResource(R.drawable.green_tick);
            orderFulfilledCardMat.setStrokeColor(Color.argb(175, 00, 80, 0));
            orderFulfilledText.setText("Current Order amount has fulfilled Minimum Order amount.");
            orderFulfilledText.setTextColor(Color.argb(175, 00, 80, 0));
        }

        if (expiryCountdown.equals("0d left") && !minFulfilled){
            optionsLayout.setClickable(false);
            expiryLayout.setVisibility(View.VISIBLE);

        }


    }
}
