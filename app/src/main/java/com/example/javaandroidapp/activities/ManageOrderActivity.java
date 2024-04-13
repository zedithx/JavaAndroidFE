package com.example.javaandroidapp.activities;

import static com.example.javaandroidapp.activities.ViewOrderDetailsActivity.setOrderStatusTypeface;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.modals.Listing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.api.Distribution;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class ManageOrderActivity extends AppCompatActivity {
    public boolean minFulfilled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_order);

        Listing listing = (Listing) getIntent().getSerializableExtra("listing");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("listings").document(listing.getUid());

        ImageButton backBtn = findViewById(R.id.backBtn);
        TextView listingName = findViewById(R.id.listingName);
        TextView orderFulfilledText = findViewById(R.id.orderFulfilledText);
        MaterialCardView orderFulfilledCardMat = findViewById(R.id.orderFulfilledCard);
        ImageView fulfilImage = findViewById(R.id.fulfilImage);
        TextView currentOrder = findViewById(R.id.currentOrder);
        TextView minOrder = findViewById(R.id.minOrder);
        TextView expiryText = findViewById(R.id.expiryDate);
        ImageView productImg = findViewById(R.id.listingImage);
        MaterialCardView expiryLayout = findViewById(R.id.expiryCard);
        ArrayList<TextView> orderStatuses = new ArrayList<>();
        LinearLayout progressParent = findViewById(R.id.progressParentLayout);
        LinearLayout optionsLayout0 = findViewById(R.id.options_layout_0);
        LinearLayout optionsLayout = findViewById(R.id.options_layout);
        LinearLayout optionsLayout2 = findViewById(R.id.options_layout_2);
        LinearLayout optionsLayout3 = findViewById(R.id.options_layout_3);
        Switch finalisedGroupOrderSwitch = findViewById(R.id.finalise_group_order_switch);
        Switch dispatchedSwitch = findViewById(R.id.dispatched_switch);
        Switch readySwitch = findViewById(R.id.ready_switch);
        String deliveryStatus = listing.getDeliveryStatus();
        boolean minFulfilled = false;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile = new Intent(ManageOrderActivity.this, MyListingActivity.class);
                startActivity(toProfile);
            }
        });

        listingName.setText(listing.getName());
        int minOrderNum = listing.getMinOrder().intValue();
        int currOrderNum = listing.getCurrentOrder().intValue();
        minOrder.setText("" + minOrderNum);
        currentOrder.setText("" + currOrderNum);
        String expiryCountdown = listing.getExpiryCountdown();
        Date expiryDate = listing.getExpiry();
        expiryText.setText(expiryCountdown);
        new ImageLoadTask(listing.getImageList().get(0), productImg).execute();

        if (currOrderNum >= minOrderNum) {
            minFulfilled = true;
            if (deliveryStatus.equals("Unfulfilled")){
                deliveryStatus = "FulfilledMinOrder";
                docRef.update("deliveryStatus", deliveryStatus);
            }
            fulfilImage.setImageResource(R.drawable.green_tick);
            orderFulfilledCardMat.setStrokeColor(Color.argb(175, 00, 80, 0));
            orderFulfilledText.setText("Current Order amount has fulfilled Minimum Order amount.");
            orderFulfilledText.setTextColor(Color.argb(175, 00, 80, 0));
        }

        if (expiryDate.before(new Date()) && !minFulfilled) {
            progressParent.setClickable(false);
            expiryLayout.setVisibility(View.VISIBLE);
            progressParent.setVisibility(View.GONE);
        } else {
            expiryLayout.setVisibility(View.GONE);
            progressParent.setVisibility(View.VISIBLE);
        }
        //TODO: remove test case
        switch (deliveryStatus) {
            case "Unfulfilled":
                optionsLayout0.setVisibility(View.VISIBLE);
                optionsLayout.setVisibility(View.GONE);
                optionsLayout2.setVisibility(View.GONE);
                optionsLayout3.setVisibility(View.GONE);
                optionsLayout0.setClickable(false);
                break;
            case "FulfilledMinOrder":
                optionsLayout0.setVisibility(View.GONE);
                optionsLayout.setVisibility(View.VISIBLE);
                optionsLayout2.setVisibility(View.GONE);
                optionsLayout3.setVisibility(View.GONE);
                finalisedGroupOrderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            buttonView.setClickable(false);
                            docRef.update("deliveryStatus", "Finalised");
                        }
                    }
                });
                break;
            case "Finalised":
                optionsLayout0.setVisibility(View.GONE);
                optionsLayout.setVisibility(View.GONE);
                optionsLayout2.setVisibility(View.VISIBLE);
                optionsLayout3.setVisibility(View.GONE);
                dispatchedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            buttonView.setClickable(false);
                            docRef.update("deliveryStatus", "Dispatched");
                        }
                    }
                });
                break;
            case "Dispatched":
                optionsLayout0.setVisibility(View.GONE);
                optionsLayout.setVisibility(View.GONE);
                optionsLayout2.setVisibility(View.GONE);
                optionsLayout3.setVisibility(View.VISIBLE);
                readySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            buttonView.setClickable(false);
                            docRef.update("deliveryStatus", "Ready");
                        }
                    }
                });
                break;
            case "Ready":
                optionsLayout0.setVisibility(View.GONE);
                optionsLayout.setVisibility(View.GONE);
                optionsLayout2.setVisibility(View.GONE);
                optionsLayout3.setVisibility(View.VISIBLE);

                optionsLayout3.setClickable(false);
                break;
            default:
                optionsLayout0.setVisibility(View.VISIBLE);
                optionsLayout.setVisibility(View.GONE);
                optionsLayout2.setVisibility(View.GONE);
                optionsLayout3.setVisibility(View.GONE);
                optionsLayout0.setClickable(false);
        }
    }
}
