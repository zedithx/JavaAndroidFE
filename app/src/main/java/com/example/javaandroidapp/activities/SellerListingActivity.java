package com.example.javaandroidapp.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
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

//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int numItems = 8;
        GridLayout listingsGrid = (GridLayout) findViewById(R.id.listingsGrid);
        listingsGrid.removeAllViews();
        listingsGrid.setColumnCount(2);
        listingsGrid.setRowCount(numItems / 2 + 1);
//        View newMatCardView = inflater.inflate(R.layout.listing_card, null);

        listings = new ArrayList<>();
//        listings = seller.getListings();
//        if (listings.size() > 0){
//            Handler listingHandler = new Handler();
//        }

//        int width = listingsGrid.getWidth() / 2;
        for (int i = 0; i < numItems; i++) {
            GridLayout.LayoutParams cardMaterialParams = new GridLayout.LayoutParams();
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            cardMaterialParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            cardMaterialParams.height = 450;
            cardMaterialParams.width = 450;
            cardMaterialParams.setMargins(15, 15, 15, 15);

            CardView card = new CardView(this);
            card.setLayoutParams(cardParams);

            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(newLayoutParams);
            layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout imgLayout = new LinearLayout(this);
            imgLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));
            imgLayout.setClipChildren(true);
            ImageView cardImg = new ImageView(this);
            cardParams.setMargins(0, 0, 0, 0);
            cardImg.setLayoutParams(cardParams);
            cardImg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            TextView cardTitle = new TextView(this);
            TextView cardDescription = new TextView(this);
            cardTitle.setTextSize(20);
            cardTitle.setEllipsize(TextUtils.TruncateAt.END);
            cardTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            cardDescription.setText("S$ 40.80");
            if (i % 3 == 0) { //testing with various images
                cardImg.setImageResource(R.drawable.test_kangol);
            } else {
                cardImg.setImageResource(R.drawable.test_teenageengineering);

            }
            cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cardTitle.setText("" + i + "th item");
            imgLayout.addView(cardImg);
            layout.addView(imgLayout);
            layout.addView(cardTitle);
            layout.addView(cardDescription);
            card.addView(layout);

            MaterialCardView cardMat = new MaterialCardView(this);
            cardMaterialParams.rowSpec = GridLayout.spec(i / 2);
            cardMaterialParams.columnSpec = GridLayout.spec(i % 2);
            cardMat.setLayoutParams(cardMaterialParams);
            cardMat.setStrokeWidth(0);
            cardMat.addView(card);
            listingsGrid.addView(cardMat);
        }

//        int total = 8;
//        int column = 2;
//        int row = total / column;
//        listingsGrid.setColumnCount(column);
//        listingsGrid.setRowCount(row + 1);
//        for(int i =0, c = 0, r = 0; i < total; i++, c++)
//        {
//            if(c == column)
//            {
//                c = 0;
//                r++;
//            }
//            ImageView oImageView = new ImageView(this);
//            oImageView.setImageResource(R.drawable.test_teenageengineering);
//            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
//            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
//            param.width = ViewGroup.LayoutParams.FILL_PARENT;
//            param.setMargins(15, 15, 15, 15);
//            param.setGravity(Gravity.CENTER);
//            param.columnSpec = GridLayout.spec(c);
//            param.rowSpec = GridLayout.spec(r);
//            oImageView.setLayoutParams (param);
//            listingsGrid.addView(oImageView);
//        }
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