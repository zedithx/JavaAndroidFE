package com.example.javaandroidapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaandroidapp.R;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.objects.User;
import com.google.android.material.card.MaterialCardView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;


public class SellerListingActivity extends AppCompatActivity {

    public static User seller;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // get user object from seller clicked
        seller = (User) getIntent().getSerializableExtra("user");
        setContentView(R.layout.view_pdt_owner_listing);

        GridLayout listingsGrid = findViewById(R.id.listingsGrid);
        ArrayList<Listing> listings = seller.getListings();
        if (listings.size() > 0){
            Handler listingHandler = new Handler();
        }





    }
}

// don't need another class, just use listing object
class ListingCard{
    CardView card;
    MaterialCardView materialCard;
    String title;
    int minOrder;
    int imageList; // get first image only
    Date expiry;
    Double price;

}