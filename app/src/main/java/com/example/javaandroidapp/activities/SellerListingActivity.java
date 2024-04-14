package com.example.javaandroidapp.activities;

import static com.example.javaandroidapp.activities.ViewProductActivity.df;
import static com.example.javaandroidapp.activities.ViewProductActivity.productDescription;
import static com.example.javaandroidapp.modals.Listing.createListingWithDocumentSnapshot;
import static com.example.javaandroidapp.modals.Listing.createListingWithDocumentSnapshot;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;

import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.User;
import com.example.javaandroidapp.utils.ChatSystem;

import com.example.javaandroidapp.utils.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.models.Channel;
import io.getstream.chat.android.state.plugin.config.StatePluginConfig;
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory;
import io.getstream.chat.java.exceptions.StreamException;


public class SellerListingActivity extends AppCompatActivity {

    public static int count = 0;
    ApplicationInfo applicationInfo;
    String apiKey;
    String sellerName;
    String sellerUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try {
            applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (applicationInfo != null){
            apiKey = applicationInfo.metaData.getString("apiKey");
        }
        Listing listingExtra = (Listing) getIntent().getSerializableExtra("listing");
        setContentView(R.layout.view_pdt_owner_listing);
        sellerName = listingExtra.getCreatedBy();

        TextView ownerTextView = findViewById(R.id.owner);
        ImageView profilePic = findViewById(R.id.profile_pic);
        ownerTextView.setText(listingExtra.getCreatedBy());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ChatSystem chatSystem = ChatSystem.getInstance(getApplicationContext(), uid);

        db.collection("users").whereEqualTo("name", listingExtra.getCreatedBy()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot doc = task.getResult();
                    if (!doc.isEmpty()){
                        DocumentSnapshot docSnapshot = doc.getDocuments().get(0);
                        String profilePicStringURL = docSnapshot.getString("profileImage");
                        if (profilePicStringURL.length() > 0) {
//                            new ImageLoadTask(profilePicStringURL, profilePic).execute();
                            Glide.with(SellerListingActivity.this).load(profilePicStringURL).into(profilePic);

                        }
                    }
                }
            }
        });
        db.collection("listings").whereEqualTo("createdBy", sellerName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int numItems = task.getResult().size();
                if (task.isSuccessful()) {
                    GridLayout listingsGrid = (GridLayout) findViewById(R.id.listingsGrid);
                    listingsGrid.removeAllViews();
                    listingsGrid.setColumnCount(2);
                    listingsGrid.setRowCount(numItems);
                    int count = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            String expiry = document.getString("expiryCountdown");
                            Listing listing = createListingWithDocumentSnapshot(document);
                            listingsGrid.addView(createNewMatCard(count, listing));
                            count += 1;
                        }
                    }
                }
            }
        });

        ImageButton chatBtn = findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = new ArrayList<>();
                //get person uid
                list.add(uid);
                Users.getUserFromId(db, listingExtra.getCreatedById(), new CallbackAdapter(){
                    @Override
                    public void getUser(User new_user) {
                        sellerUserId = new_user.getUid();
                    }
                });
                list.add(sellerUserId);
                try {
                    chatSystem.createChannel(list, new CallbackAdapter() {
                        @Override
                        public void getChannel(Channel channel) {
                            startActivity(ChatActivity.newIntent(SellerListingActivity.this, channel));
                        }
                    });
                }
                catch (StreamException e) {
                    throw new RuntimeException(e);
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

    public MaterialCardView createNewMatCard(int count, Listing listing) {

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
        cardTitle.setMaxLines(1);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(25, 10, 25, 0);
        cardTitle.setLayoutParams(textParams);

        // Price of Product displayed on each card
        TextView cardPrice = new TextView(SellerListingActivity.this);
        cardPrice.setLayoutParams(textParams);
        cardPrice.setTextSize(15);
        cardPrice.setText("S$" + df.format(listing.getPrice()));
        cardPrice.setTypeface(Typeface.DEFAULT_BOLD);

        // Horizontal Layout for order num text and icon
        LinearLayout numOrderLayout = new LinearLayout(SellerListingActivity.this);
        numOrderLayout.setOrientation(LinearLayout.HORIZONTAL);
        numOrderLayout.setLayoutParams(textParams);

        // Num of orders displayed on each card
        int currOrderNum = listing.getCurrentOrder().intValue();
        int minOrderNum = listing.getMinOrder().intValue();
        TextView cardOrderNum = new TextView(SellerListingActivity.this);
        cardOrderNum.setTextSize(12);
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
        expiryText.setTextSize(12);
        expiryText.setTextColor(Color.RED);
        if (listing.getExpiry().after(new Date())) {
            expiryText.setText(listing.getExpiryCountdown());
        }else{
            expiryText.setText("Expired");
        }
        expiryText.setLayoutParams(textParams);

//        new ImageLoadTask(listing.getImageList().get(0), cardImg).execute();
        Glide.with(SellerListingActivity.this).load(listing.getImageList().get(0)).into(cardImg);


        cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cardTitle.setText(listing.getName());
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

        cardMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getProduct = new Intent(SellerListingActivity.this, TransitionViewProductActivity.class);
                getProduct.putExtra("listing", listing);
                startActivity(getProduct);
            }
        });
        cardMaterialParams.rowSpec = GridLayout.spec(count / 2);
        cardMaterialParams.columnSpec = GridLayout.spec(count % 2);
        cardMat.setLayoutParams(cardMaterialParams);
        cardMat.addView(card);
        return cardMat;
    }
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
