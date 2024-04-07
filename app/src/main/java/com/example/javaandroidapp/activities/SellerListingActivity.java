package com.example.javaandroidapp.activities;

import static com.example.javaandroidapp.activities.ViewProductActivity.df;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaandroidapp.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;


public class SellerListingActivity extends AppCompatActivity {

    public static String sellerEmail;
    //    ArrayList<String> listingsIdList;
    public static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // get sellerEmail from extra
        sellerEmail = (String) getIntent().getSerializableExtra("sellerEmail");
        setContentView(R.layout.view_pdt_owner_listing);

        TextView ownerTextView = findViewById(R.id.owner);
        ownerTextView.setText(sellerEmail);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("listings").whereEqualTo("createdBy", sellerEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int numItems = task.getResult().size();
                if (task.isSuccessful()) {
                    GridLayout listingsGrid = (GridLayout) findViewById(R.id.listingsGrid);
                    listingsGrid.removeAllViews();
                    listingsGrid.setColumnCount(2);
                    listingsGrid.setRowCount(numItems);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {

                            String listingId = document.getId();
                            String productName = document.getString("name");
                            String price = "S$" + df.format(document.getDouble("price"));
                            String expiry = document.getString("expiryCountdown");
                            List<String> imageList = (List<String>) document.get("imageList");

                            int minOrder = document.getDouble("minOrder").intValue();
                            int currOrder = document.getDouble("currentOrder").intValue();
                            Log.d("listingDetails", "" + listingId + "\n" + productName + "\n" + price + "\n" + expiry + "\n" + "minOrder: " + minOrder + "\n" + "currentOrder: " + currOrder + "\n" + "img str: " + imageList.get(0));


                            listingsGrid.addView(createNewMatCard(count, productName, price, minOrder, currOrder, expiry, imageList));
                            count += 1;
                        }


// change this


                    }

                }
            }
        });


        // close activity when back btn clicked
        ImageButton backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public MaterialCardView createNewMatCard(int count, String name, String price, int minOrder, int currentOrder, String expiry, List<String> imageList) {

        GridLayout.LayoutParams cardMaterialParams = new GridLayout.LayoutParams();
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        cardMaterialParams.height = 750;
        cardMaterialParams.width = 450;
        cardMaterialParams.setMargins(15, 15, 15, 15);
        CardView card = new CardView(SellerListingActivity.this);
        card.setRadius(20);
        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(Color.TRANSPARENT);
        card.setCardElevation(0);


        LinearLayout layout = new LinearLayout(SellerListingActivity.this);
        layout.setLayoutParams(newLayoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);


        LinearLayout imgLayout = new LinearLayout(SellerListingActivity.this);
        imgLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        imgLayout.setClipChildren(true);
        ImageView cardImg = new ImageView(SellerListingActivity.this);
        cardParams.setMargins(0, 0, 0, 0);
        cardImg.setLayoutParams(cardParams);
        cardImg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));

        // Name of product displayed on each card
        TextView cardTitle = new TextView(SellerListingActivity.this);
        cardTitle.setTextSize(15);
        cardTitle.setEllipsize(TextUtils.TruncateAt.END);
        cardTitle.setMaxLines(2);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(25, 10, 25, 0);
        cardTitle.setLayoutParams(textParams);

        // Price of Product displayed on each card
        TextView cardPrice = new TextView(SellerListingActivity.this);
        cardPrice.setLayoutParams(textParams);
        cardPrice.setTextSize(20);
        cardPrice.setText(price);
        cardPrice.setTypeface(Typeface.DEFAULT_BOLD);

        // Horizontal Layout for order num text and icon
        LinearLayout numOrderLayout = new LinearLayout(SellerListingActivity.this);
        numOrderLayout.setOrientation(LinearLayout.HORIZONTAL);
        numOrderLayout.setLayoutParams(textParams);

        // Num of orders displayed on each card
        int currOrderNum = currentOrder;
        int minOrderNum = minOrder;
        TextView cardOrderNum = new TextView(SellerListingActivity.this);
        cardOrderNum.setTextSize(15);
        cardOrderNum.setText(currOrderNum + "/" + minOrderNum);
        cardOrderNum.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Num of orders icon
        ImageView pplIcon = new ImageView(SellerListingActivity.this);
        pplIcon.setLayoutParams(textParams);
        pplIcon.setImageResource(R.drawable.people);
        pplIcon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        numOrderLayout.addView(cardOrderNum);
        numOrderLayout.addView(pplIcon);

        // Expiry time displayed on each card
        TextView expiryText = new TextView(SellerListingActivity.this);
        expiryText.setTextSize(15);
        expiryText.setTextColor(Color.RED);
        expiryText.setText(expiry);
        expiryText.setLayoutParams(textParams);


//        cardImg.setImageURI(imageList.get(0));
        new ImageLoadTask(imageList.get(0), cardImg).execute();


        cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cardTitle.setText(name);
        imgLayout.addView(cardImg);
        layout.addView(imgLayout);
        layout.addView(cardTitle);
        layout.addView(cardPrice);
        layout.addView(numOrderLayout);
        layout.addView(expiryText);
        card.addView(layout);

        MaterialCardView cardMat = new MaterialCardView(SellerListingActivity.this);
        cardMat.setClickable(true);
        cardMat.setClipChildren(true);
        cardMat.setCardBackgroundColor(null);
        cardMat.setCardElevation(0);
        cardMat.setStrokeWidth(0);

        cardMat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                Intent viewProductIntent = new Intent(SellerListingActivity.this, TransitionViewProductActivity.class);
//                listing =
//                viewProductIntent.putExtra("listing", );
//                startActivity(viewProductIntent);
            }
        });
        cardMaterialParams.rowSpec = GridLayout.spec(count / 2);
        cardMaterialParams.columnSpec = GridLayout.spec(count % 2);
        cardMat.setLayoutParams(cardMaterialParams);
        cardMat.addView(card);
        return cardMat;
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
class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView imageView;

    public ImageLoadTask(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
    }

}
