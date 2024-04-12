package com.example.javaandroidapp.activities;

import static com.example.javaandroidapp.activities.ViewProductActivity.df;
import static com.example.javaandroidapp.activities.ViewProductActivity.productDescription;
import static com.example.javaandroidapp.modals.Listing.createListingWithDocumentSnapshot;
import static com.example.javaandroidapp.modals.Listing.createListingWithDocumentSnapshot;

import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaandroidapp.R;

import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.utils.ChatSystem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.firestore.model.Document;

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


public class MyListingActivity extends AppCompatActivity {

    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // get sellerEmail from extra
        setContentView(R.layout.my_listings);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();


        TextView ownerTextView = findViewById(R.id.owner);
        TextView emailTextView = findViewById(R.id.email);
        TextView noListingToShowText = findViewById(R.id.noListingsToShow);
        TextView numberOfListingsText = findViewById(R.id.number_of_listings);
        ImageView profilePic = findViewById(R.id.profile_pic);
        ImageButton backBtn = findViewById(R.id.backBtn);
        ImageButton addListingBtn = findViewById(R.id.add_listing_btn);
        MaterialCardView editInfoCardMat = findViewById(R.id.edit_info_card_mat);
        ImageButton settingsBtn = findViewById(R.id.settings);


        DocumentReference userDocRef = db.collection("users").document(fbUser.getUid());
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDoc = task.getResult();
                    if (userDoc.exists()) {
                        name = userDoc.getString("name");
                        ownerTextView.setText(name);
                        emailTextView.setText(fbUser.getEmail());
                        db.collection("listings").whereEqualTo("createdBy", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (!querySnapshot.isEmpty()) {
                                        noListingToShowText.setVisibility(View.GONE);
                                        int querySize = querySnapshot.size();
                                        int count = 0;
                                        numberOfListingsText.setText("" + querySize);
                                        GridLayout listingsGrid = (GridLayout) findViewById(R.id.listingsGrid);
                                        listingsGrid.removeAllViews();
                                        listingsGrid.setColumnCount(2);
                                        listingsGrid.setRowCount(querySize);
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.exists()) {
                                                Listing listing = createListingWithDocumentSnapshot(document);
                                                listingsGrid.addView(createNewMatCard(count, listing));
                                                count += 1;
                                            }
                                        }


                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        // close activity when back btn clicked
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(MyListingActivity.this, TransitionLandingActivity.class);
                startActivity(main);
            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(MyListingActivity.this, SettingsActivity.class);
                startActivity(Main);
            }
        });
        db.collection("users").document(fbUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot docSnapshot = task.getResult();
                    if (docSnapshot.exists()) {
                        String profilePicStringURL = docSnapshot.getString("profileImage");
                        if (profilePicStringURL.length() > 0) {
                            new ImageLoadTask(profilePicStringURL, profilePic).execute();
                        }
                    }
                }
            }
        });
        editInfoCardMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(MyListingActivity.this, EditInfoActivity.class);
                startActivity(Main);
            }
        });
        addListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(MyListingActivity.this, AddListingActivity.class);
                startActivity(Main);
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
        CardView card = new CardView(this);
        card.setRadius(20);
        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(Color.TRANSPARENT);
        card.setCardElevation(0);


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

        // Horizontal Layout for chat number and chat icon
        LinearLayout chatNumLayout = new LinearLayout(this);
        chatNumLayout.setOrientation(LinearLayout.HORIZONTAL);
        chatNumLayout.setLayoutParams(textParams);

        // number of chats of Product displayed on each card
        int chats = 3;
        TextView chatNumText = new TextView(this);
        chatNumText.setTextColor(Color.RED);
        chatNumText.setLayoutParams(textParams);
        chatNumText.setTextSize(15);
        chatNumText.setText("" + chats);
        chatNumText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        // Chat icon image beside chat number displayed on card
        ImageView chatIcon = new ImageView(this);
        chatIcon.setImageResource(R.drawable.red_chatbox);
        LinearLayout.LayoutParams chatIconParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        chatIcon.setPadding(5, 0, 0, 0);
        chatIcon.setLayoutParams(chatIconParams);
        chatIcon.setScaleX((float) 0.7);
        chatIcon.setScaleY((float) 0.7);
        chatNumLayout.addView(chatNumText);
        chatNumLayout.addView(chatIcon);

        // Horizontal Layout for order num text and icon
        LinearLayout numOrderLayout = new LinearLayout(this);
        numOrderLayout.setOrientation(LinearLayout.HORIZONTAL);
        numOrderLayout.setLayoutParams(textParams);

        // Num of orders displayed on each card
        int currOrderNum = listing.getCurrentOrder().intValue();
        int minOrderNum = listing.getMinOrder().intValue();
        TextView cardOrderNum = new TextView(this);
        cardOrderNum.setTextSize(15);
        cardOrderNum.setText(currOrderNum + "/" + minOrderNum);
        cardOrderNum.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Num of orders icon
        ImageView pplIcon = new ImageView(this);
        pplIcon.setImageResource(R.drawable.people);
        pplIcon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        numOrderLayout.addView(cardOrderNum);
        numOrderLayout.addView(pplIcon);

        // Expiry time displayed on each card
        TextView expiryText = new TextView(this);
        expiryText.setTextSize(15);
        expiryText.setTextColor(Color.RED);
        expiryText.setText(listing.getExpiryCountdown());
        expiryText.setLayoutParams(textParams);

        new ImageLoadTask(listing.getImageList().get(0), cardImg).execute();


        cardImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cardTitle.setText(name);
        imgLayout.addView(cardImg);
        layout.addView(imgLayout);
        layout.addView(cardTitle);
        layout.addView(chatNumLayout);
        layout.addView(numOrderLayout);
        layout.addView(expiryText);
        card.addView(layout);

        MaterialCardView cardMat = new MaterialCardView(this);
        cardMat.setClickable(true);
        cardMat.setClipChildren(true);
        cardMat.setCardBackgroundColor(null);
        cardMat.setCardElevation(0);
        cardMat.setStrokeWidth(0);
        cardMat.setOnClickListener(new View.OnClickListener() {
            //TODO: Change Button to onClick -> view listing orders
            @Override
            public void onClick(View v) {
                Intent getProduct = new Intent(MyListingActivity.this, ManageOrderActivity.class);
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

