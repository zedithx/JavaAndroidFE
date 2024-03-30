package com.example.javaandroidapp.activities;

import static androidx.fragment.app.FragmentManager.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.objects.Listing;
import com.google.android.material.card.MaterialCardView;

import org.w3c.dom.Text;

public class OrderConfirmationActivity extends AppCompatActivity {
    ImageView listingImageView;
    TextView listingNameTextView;
    TextView orderAmountTextView;
    TextView variantNameTextView;
    LinearLayout imageViewLayout;
    ImageButton backBtn;
    CardView confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Bundle bundle = getIntent().getExtras();
        System.out.println(bundle);
        MakeOrder orderDetails = (MakeOrder) bundle.getSerializable("new_order");
//        Listing listing = orderDetails.getListing();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirmation);
//
//
//
//        listingNameTextView = findViewById(R.id.productName);
//        imageViewLayout = findViewById(R.id.imageViewLayout);
//        listingImageView = findViewById(R.id.listingImage);
//        variantNameTextView = findViewById(R.id.variant_name);
//        orderAmountTextView = findViewById(R.id.variant_amt);
//
//        listingNameTextView.setText(listing.getName());
//        Glide.with(imageViewLayout).load(listing.getImageList().get(0)).into(listingImageView);
//        variantNameTextView.setText(orderDetails.getVariantName());
//        orderAmountTextView.setText("" + orderDetails.getAmount());
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirmBtn = findViewById(R.id.confirmButton);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make stripe payment
            }
        });
    }
}
