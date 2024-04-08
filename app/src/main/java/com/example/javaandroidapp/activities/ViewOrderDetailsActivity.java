package com.example.javaandroidapp.activities;

import static com.example.javaandroidapp.activities.ViewProductActivity.df;
import static com.example.javaandroidapp.activities.ViewProductActivity.listing;

import android.content.Intent;

import android.graphics.Canvas;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class ViewOrderDetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.view_order_details);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        Order orderDetails = (Order) getIntent().getSerializableExtra("Order");
        ImageView processProgress = findViewById(R.id.process_progress);
        RelativeLayout productDetailsLayout = findViewById(R.id.product_details_layout);
        LinearLayout variationNameLayout = findViewById(R.id.variation_name_layout);
        LinearLayout variationAmountLayout = findViewById(R.id.variation_amount_layout);
        LinearLayout qrCodeLayout = findViewById(R.id.qr_code_layout);
//        LinearLayout orderUnprocessed = findViewById(R.id.order_unprocessed);
        ImageView unprocessedIcon = findViewById(R.id.unprocessed_icon);
        TextView unprocessedText = findViewById(R.id.unprocessed_text);
        ImageButton backBtn = findViewById(R.id.backBtn);
        MaterialCardView collectionPageBtn = findViewById(R.id.collection_btn);
        MaterialCardView backToMyPageBtn = findViewById(R.id.back_to_my_page_btn);
        CardView backToMyPageCard = findViewById(R.id.back_to_my_page_card);
        TextView backToMyPageText = findViewById(R.id.back_to_my_page_text);
        TextView orderName = findViewById(R.id.orderName);
        TextView sellerName = findViewById(R.id.seller);
        TextView variantTextView = findViewById(R.id.order_variant);
//        TextView expiryTextView = findViewById(R.id.date);
        ImageView productImage = findViewById(R.id.product_image);
        String deliveryStatus = orderDetails.getDelivery();

        TextView variationNameText = new TextView(this);
        TextView variationAmountText = new TextView(this);

        variationNameText.setText(orderDetails.getVariant());
        variationAmountText.setText("x" + orderDetails.getQuantity());
        variationNameLayout.addView(variationNameText);
        variationAmountLayout.addView(variationAmountText);

//        //TODO: hardcode testing, remove the hardcoded deliveryStatus when done
//        deliveryStatus = "Ready";

        switch (deliveryStatus) {
            case "Unfulfilled":
                processProgress.setVisibility(View.VISIBLE);
                processProgress.setImageResource(R.drawable.process_progress_1);
                unprocessedIcon.setImageResource(R.drawable.error);
                collectionPageBtn.setVisibility(View.GONE);
                backToMyPageBtn.setStrokeWidth(0);
                backToMyPageCard.setCardBackgroundColor(Color.RED);
                backToMyPageText.setTextColor(Color.WHITE);
                unprocessedText.setText("Waiting For Group Order To Finalise");
                break;
            case "Finalised":
                processProgress.setVisibility(View.VISIBLE);
                processProgress.setImageResource(R.drawable.progress_process_2);
                unprocessedIcon.setImageResource(R.drawable.tick);
                collectionPageBtn.setVisibility(View.GONE);
                backToMyPageBtn.setStrokeWidth(0);
                backToMyPageCard.setCardBackgroundColor(Color.RED);
                backToMyPageText.setTextColor(Color.WHITE);
                unprocessedText.setText("Order Finalised");
                break;
            case "Dispatched":
                processProgress.setVisibility(View.VISIBLE);
                processProgress.setImageResource(R.drawable.progress_process_3);
                unprocessedIcon.setImageResource(R.drawable.tick);
                collectionPageBtn.setVisibility(View.GONE);
                backToMyPageBtn.setStrokeWidth(0);
                backToMyPageCard.setCardBackgroundColor(Color.RED);
                backToMyPageText.setTextColor(Color.WHITE);
                unprocessedText.setText("Order Dispatched");
                break;
            case "Ready":
                processProgress.setImageResource(R.drawable.progress_process_4);
                unprocessedIcon.setImageResource(R.drawable.tick);
                unprocessedText.setText("Ready For Collection");
                collectionPageBtn.setVisibility(View.VISIBLE);
                collectionPageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toCollectionActivity = new Intent(ViewOrderDetailsActivity.this, CollectionActivity.class);
                        toCollectionActivity.putExtra("Order", orderDetails);
                        startActivity(toCollectionActivity);
                    }
                });
                backToMyPageBtn.setStrokeWidth(3);
                backToMyPageBtn.setStrokeColor(Color.RED);
                backToMyPageCard.setCardBackgroundColor(Color.TRANSPARENT);
                backToMyPageCard.setCardElevation(0);
                backToMyPageText.setTextColor(Color.RED);


                break;
            default:
                processProgress.setVisibility(View.VISIBLE);
                processProgress.setImageResource(R.drawable.process_progress);
                unprocessedIcon.setImageResource(R.drawable.error);
                collectionPageBtn.setVisibility(View.GONE);
                backToMyPageBtn.setStrokeWidth(0);
                backToMyPageCard.setCardBackgroundColor(Color.RED);
                backToMyPageText.setTextColor(Color.WHITE);
                unprocessedText.setText("Error Loading Order");
        }
        productDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent viewProduct = new Intent(ViewOrderDetailsActivity.this, ViewProductActivity.class);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backToMyPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(ViewOrderDetailsActivity.this, TransitionLandingActivity.class);
                startActivity(Main);
            }
        });

        DocumentReference docRef = db.collection("listings").document(orderDetails.getListingId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        orderName.setText(document.getString("name"));
                        sellerName.setText(document.getString("createdBy"));
                        variantTextView.setText("Variant: " + orderDetails.getVariant());
//                        expiryTextView.setText(document.getString("expiryCountdown"));
                        new ImageLoadTask(((ArrayList<String>) document.get("imageList")).get(0), productImage).execute();

                    }
                }
            }
        });


    }
}
