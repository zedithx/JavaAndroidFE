package com.example.javaandroidapp.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

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

        int numItems = 8;
        GridLayout listingsGrid = (GridLayout) findViewById(R.id.listingsGrid);
        listingsGrid.removeAllViews();
        listingsGrid.setColumnCount(2);
        listingsGrid.setRowCount(numItems / 2 + 1);

        listings = new ArrayList<>();

        for (int i = 0; i < numItems; i++) {
            GridLayout.LayoutParams cardMaterialParams = new GridLayout.LayoutParams();
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            cardMaterialParams.height = 750;
            cardMaterialParams.width = 450;
            cardMaterialParams.setMargins(15, 15, 15, 15);

            CardView card = new CardView(this);
            card.setRadius(20);
            card.setLayoutParams(cardParams);

            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(newLayoutParams);
            layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout imgLayout = new LinearLayout(this);
            imgLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            imgLayout.setClipChildren(true);
            ImageView cardImg = new ImageView(this);
            cardParams.setMargins(0, 0, 0, 0);
            cardImg.setLayoutParams(cardParams);
            cardImg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));

            // Name of product displayed on each card
            TextView cardTitle = new TextView(this);
            cardTitle.setTextSize(15);
            cardTitle.setEllipsize(TextUtils.TruncateAt.END);
            cardTitle.setMaxLines(2);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.setMargins(25, 10, 25, 0);
            cardTitle.setLayoutParams(textParams);

            // Price of Product displayed on each card
            TextView cardPrice = new TextView(this);
            cardPrice.setLayoutParams(textParams);
            cardPrice.setTextSize(20);
            cardPrice.setText("S$" + ViewProductActivity.df.format(40.80));
            cardPrice.setTypeface(Typeface.DEFAULT_BOLD);

            // Horizontal Layout for order num text and icon
            LinearLayout numOrderLayout = new LinearLayout(this);
            numOrderLayout.setOrientation(LinearLayout.HORIZONTAL);
            numOrderLayout.setLayoutParams(textParams);

            // Num of orders displayed on each card
            int currOrderNum = 20;
            int minOrderNum = 40;
            TextView cardOrderNum = new TextView(this);
            cardOrderNum.setTextSize(15);
            cardOrderNum.setText(currOrderNum + "/" + minOrderNum);
            cardOrderNum.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            // Num of orders icon
            ImageView pplIcon = new ImageView(this);
            pplIcon.setLayoutParams(textParams);
            pplIcon.setImageResource(R.drawable.people);
            pplIcon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

            numOrderLayout.addView(cardOrderNum);
            numOrderLayout.addView(pplIcon);

            // Expiry time displayed on each card
            int expiry = 4;
            TextView expiryText = new TextView(this);
            expiryText.setTextSize(15);
            expiryText.setTextColor(Color.RED);
            expiryText.setText(expiry + " days left");
            expiryText.setLayoutParams(textParams);

            if (i % 3 == 0) { //testing with various images
                cardImg.setImageResource(R.drawable.test_kangol);
            } else {
                cardImg.setImageResource(R.drawable.test_springheads);

            }
            cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cardTitle.setText("" + i + "th item Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum");
            imgLayout.addView(cardImg);
            layout.addView(imgLayout);
            layout.addView(cardTitle);
            layout.addView(cardPrice);
            layout.addView(numOrderLayout);
            layout.addView(expiryText);
            card.addView(layout);

            MaterialCardView cardMat = new MaterialCardView(this);
            cardMat.setClickable(true);
            cardMat.setClipChildren(true);
            cardMat.setCardBackgroundColor(null);
            cardMat.setCardElevation(0);
            cardMat.setStrokeWidth(0);

            cardMat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        card.setCardElevation(20);
                        cardMat.setStrokeWidth(20);
                        cardMat.setStrokeColor(Color.RED);
                    }else{
                        card.setCardElevation(0);
                        cardMat.setStrokeWidth(0);
                    }
                }
            });
            cardMaterialParams.rowSpec = GridLayout.spec(i / 2);
            cardMaterialParams.columnSpec = GridLayout.spec(i % 2);
            cardMat.setLayoutParams(cardMaterialParams);
            cardMat.addView(card);
            listingsGrid.addView(cardMat);
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