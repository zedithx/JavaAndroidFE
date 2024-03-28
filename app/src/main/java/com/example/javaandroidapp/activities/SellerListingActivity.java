package com.example.javaandroidapp.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaandroidapp.R;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.objects.User;
import com.google.android.material.card.MaterialCardView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;


public class SellerListingActivity extends AppCompatActivity {

    public static User seller;
    ArrayList<Listing> listings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get seller of product shown in ViewProductActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String seller = extras.getString("seller");
            //The key argument here must match that used in the ViewProductActivity
        }


        // get user object from seller clicked
        seller = (User) getIntent().getSerializableExtra("seller");
        setContentView(R.layout.view_pdt_owner_listing);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        GridLayout listingsGrid = findViewById(R.id.listingsGrid);

        listings = new ArrayList<>();
//        listings = seller.getListings();
//        if (listings.size() > 0){
//            Handler listingHandler = new Handler();
//        }
        for (int i = 0; i < 8; i++) {
            View newMatCardView = inflater.inflate(R.layout.listing_card, null);
            ImageView cardImg = newMatCardView.findViewById(R.id.listingCardImage);
            TextView cardText = newMatCardView.findViewById(R.id.listingCardText);

            cardImg.setImageResource(R.drawable.test_kangol);
            cardText.setText("Test name");


        }

        // close activity when back btn clicked
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}

// don't need another class, just use listing object
class ListingPlaceholder {
    CardView card;
    MaterialCardView materialCard;
    String title;
    int minOrder;
    int imageList; // get first image only
    Date expiry;
    Double price;

}