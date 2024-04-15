package com.example.javaandroidapp.activities;

import static com.example.javaandroidapp.activities.ViewProductActivity.df;
import static com.example.javaandroidapp.activities.ViewProductActivity.listing;

import android.content.Intent;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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

import org.checkerframework.checker.units.qual.A;
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
        LinearLayout variationAmt = findViewById(R.id.variation_amt);
        LinearLayout variationItemPriceLayout = findViewById(R.id.variation_item_price_layout);
        LinearLayout variationTotalPriceLayout = findViewById(R.id.variation_total_price_layout);
        ImageView unprocessedIcon = findViewById(R.id.unprocessed_icon);
        TextView unprocessedText = findViewById(R.id.unprocessed_text);
        ArrayList<TextView> orderStatuses = new ArrayList<>();
        orderStatuses.add(findViewById(R.id.order_status_1));
        orderStatuses.add(findViewById(R.id.order_status_2));
        orderStatuses.add(findViewById(R.id.order_status_3));
        orderStatuses.add(findViewById(R.id.order_status_4));
        ImageButton backBtn = findViewById(R.id.backBtn);
        MaterialCardView collectionPageBtn = findViewById(R.id.collection_btn);
        MaterialCardView backToMyPageBtn = findViewById(R.id.back_to_my_page_btn);
        CardView backToMyPageCard = findViewById(R.id.back_to_my_page_card);
        TextView backToMyPageText = findViewById(R.id.back_to_my_page_text);
        TextView orderName = findViewById(R.id.orderName);
        TextView sellerName = findViewById(R.id.seller);
        TextView variantTextView = findViewById(R.id.order_variant);
        ImageView productImage = findViewById(R.id.product_image);
        String deliveryStatus = orderDetails.getDelivery();

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView variationNameText = new TextView(this);
        TextView variationAmountText = new TextView(this);
        TextView variationItemPriceText = new TextView(this);
        TextView variationTotalPriceText = new TextView(this);
        ArrayList<TextView> variationParams = new ArrayList<>();
        variationParams.add(variationNameText);
        variationParams.add(variationAmountText);
        variationParams.add(variationItemPriceText);
        variationParams.add(variationTotalPriceText);

        for (TextView varText : variationParams){
            varText.setLayoutParams(textParams);
            if (varText != variationNameText) {
                varText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            }
            varText.setMaxLines(2);
            varText.setEllipsize(TextUtils.TruncateAt.END);
        }

        variationNameText.setText(orderDetails.getVariant());
        variationItemPriceText.setText("S$" + df.format(orderDetails.getItemPrice()));
        variationTotalPriceText.setText("S$" + df.format(orderDetails.getItemPrice() * orderDetails.getQuantity()));
        variationAmountText.setText("x" + orderDetails.getQuantity());
        variationNameLayout.addView(variationNameText);
        variationAmt.addView(variationAmountText);
        variationItemPriceLayout.addView(variationItemPriceText);
        variationTotalPriceLayout.addView(variationTotalPriceText);

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
                setOrderStatusTypeface(orderStatuses, 0);
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
                setOrderStatusTypeface(orderStatuses, 1);
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
                setOrderStatusTypeface(orderStatuses, 2);
                break;
            case "Ready":
                processProgress.setImageResource(R.drawable.progress_process_4);
                unprocessedIcon.setImageResource(R.drawable.tick);
                unprocessedText.setText("Ready For Collection");
                collectionPageBtn.setVisibility(View.VISIBLE);
                setOrderStatusTypeface(orderStatuses, 3);
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
                unprocessedText.setText("Order Expired. Payment will be refunded.");
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
                Intent back = new Intent(ViewOrderDetailsActivity.this, LandingOrdersActivity.class);
                startActivity(back);
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent main = new Intent(ViewOrderDetailsActivity.this, LandingOrdersActivity.class);
                startActivity(main);
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
    public static void setOrderStatusTypeface(ArrayList<TextView> orderStatuses, int pos){
        for (int i = 0; i < orderStatuses.size(); i ++){
            TextView orderStatus = orderStatuses.get(i);
            if (i == pos){
                orderStatus.setTypeface(orderStatus.getTypeface(), Typeface.BOLD);
            }else{
                orderStatus.setTypeface(orderStatus.getTypeface(), Typeface.NORMAL);
            }
        }
    }
}
