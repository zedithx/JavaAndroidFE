package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.utils.OauthToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ManageOrderActivity extends AppCompatActivity {
    public boolean minFulfilled;
    String SECRET_KEY;
    ApplicationInfo applicationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_order);
        try {
            applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (applicationInfo != null){
            SECRET_KEY = applicationInfo.metaData.getString("secretKey");
        }

        Listing listing = (Listing) getIntent().getSerializableExtra("listing");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("listings").document(listing.getUid());

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent profile = new Intent(ManageOrderActivity.this, MyListingActivity.class);
                startActivity(profile);
            }
        });

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
        LinearLayout optionsLayout4 = findViewById(R.id.options_layout_4);
        Switch finalisedGroupOrderSwitch = findViewById(R.id.finalise_group_order_switch);
        Switch dispatchedSwitch = findViewById(R.id.dispatched_switch);
        MaterialCardView viewIndividualOrders = findViewById(R.id.view_indiv_orders);
        MaterialCardView getQrCode = findViewById(R.id.get_qr_code);
        String deliveryStatus = listing.getDeliveryStatus();
        boolean minFulfilled = false;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile = new Intent(ManageOrderActivity.this, MyListingActivity.class);
                startActivity(toProfile);
            }
        });
        getQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent merchantQR = new Intent(ManageOrderActivity.this, MerchantDepositActivity.class);
                merchantQR.putExtra("Listing", listing);
                startActivity(merchantQR);
            }
        });
        if (listing.getDeliveryStatus().equals("Unfulfilled") && listing.getExpiry().before(new Date())) {
            viewIndividualOrders.setClickable(false);
            CardView card = findViewById(R.id.card);
            card.setCardBackgroundColor(Color.LTGRAY);
        }else{
            viewIndividualOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent indivOrders = new Intent(ManageOrderActivity.this, IndivOrdersActivity.class);
                    indivOrders.putExtra("listing", listing);
                    startActivity(indivOrders);
                }
            });
        }
        listingName.setText(listing.getName());
        int minOrderNum = listing.getMinOrder().intValue();
        int currOrderNum = listing.getCurrentOrder().intValue();
        minOrder.setText("" + minOrderNum);
        currentOrder.setText("" + currOrderNum);
        String expiryCountdown = listing.getExpiryCountdown();
        Date expiryDate = listing.getExpiry();
        if (expiryDate.after(new Date())) {
            expiryText.setText(expiryCountdown);
        } else {
            expiryText.setText("Expired");
        }
//        new ImageLoadTask(listing.getImageList().get(0), productImg).execute();
        Glide.with(ManageOrderActivity.this).load(listing.getImageList().get(0)).into(productImg);

        if (currOrderNum >= minOrderNum) {
            minFulfilled = true;
            if (deliveryStatus.equals("Unfulfilled")) {
                deliveryStatus = "FulfilledMinOrder";
                docRef.update("deliveryStatus", deliveryStatus);
                db.collection("orders").whereEqualTo("listingId", listing.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot doc : querySnapshot) {
                                    db.collection("orders").document(doc.getId()).update("delivery", "FulfilledMinOrder");
                                }
                            }
                        }
                    }
                });
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
                optionsLayout4.setVisibility(View.GONE);
                optionsLayout0.setClickable(false);
                getQrCode.setVisibility(View.GONE);
                break;
            case "FulfilledMinOrder":
                optionsLayout0.setVisibility(View.GONE);
                optionsLayout.setVisibility(View.VISIBLE);
                optionsLayout2.setVisibility(View.GONE);
                optionsLayout3.setVisibility(View.GONE);
                optionsLayout4.setVisibility(View.GONE);
                getQrCode.setVisibility(View.GONE);
                finalisedGroupOrderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //TODO - once finalised I need to get all ids and notify them
                            buttonView.setClickable(false);
                            docRef.update("deliveryStatus", "Finalised");
                            listing.setDeliveryStatus("Finalised");
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                                // I get my listing reference
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot listingSnapshot = task.getResult();
                                        if (listingSnapshot != null) {
                                            // I get the array of orderReference
                                            ArrayList<DocumentReference> ordersList = (ArrayList<DocumentReference>) listingSnapshot.getData().get("orders");
                                            Log.d("ordersList", "ordersList" + ordersList);
                                            // Check if the ordersList is not null and not empty before looping through it
                                            if (ordersList != null && !ordersList.isEmpty()) {
                                                // iterate through the order object
                                                for (DocumentReference orderRef : ordersList) {
                                                    orderRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            Log.d("orderRef", "orderRef" + orderRef.getId());
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot orderSnapshot = task.getResult();
                                                                if (orderSnapshot != null) {
                                                                    //use order.clientSecret to capture amount
                                                                    captureIntent(orderSnapshot.getString("clientSecret"));
                                                                    //get user object for device token for fcm
                                                                    DocumentReference userRef = (DocumentReference) orderSnapshot.getData().get("user");
                                                                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()) {
                                                                                DocumentSnapshot userSnapshot = task.getResult();
                                                                                if (userSnapshot != null) {
                                                                                    String deviceToken = (String) userSnapshot.getData().get("userIdToken");
                                                                                    Log.d("userIdToken", "userIdToken" +  deviceToken);
                                                                                    //now we have userIdToken
                                                                                    //we need to use this to generate notif
                                                                                    // Get OauthToken
                                                                                    OauthToken.getAccessToken(getApplicationContext(), new OauthToken.AccessTokenCallback(){
                                                                                        @Override
                                                                                        public void onSuccess(String new_accessToken) {
                                                                                            String nameString = listingSnapshot.get("name").toString();
                                                                                            String pricePaid = orderSnapshot.get("paidAmount").toString();
                                                                                            // Handle the access token
                                                                                            JSONObject messageObject = new JSONObject();
                                                                                            JSONObject notificationObject = new JSONObject();
                                                                                            JSONObject payloadObject = new JSONObject();

                                                                                            try {
                                                                                                // Note: I lost a year of lifespan doing this token stuff
                                                                                                messageObject.put("token", deviceToken);
                                                                                                notificationObject.put("body", String.format("Your order %s has been finalised by the seller. " +
                                                                                                        "S$%s has been deducted from your account. " +
                                                                                                        "Do check the order detail page to track your delivery", nameString, pricePaid));
                                                                                                notificationObject.put("title", "Your order has been finalised!");
                                                                                                messageObject.put("notification", notificationObject);

                                                                                                payloadObject.put("message", messageObject);

                                                                                                // Create the request
                                                                                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                                                                                        "https://fcm.googleapis.com/v1/projects/bulkifydb/messages:send",
                                                                                                        payloadObject,
                                                                                                        new Response.Listener<JSONObject>() {
                                                                                                            @Override
                                                                                                            public void onResponse(JSONObject response) {
                                                                                                                //Add toast to log total amount captured
                                                                                                            }
                                                                                                        },
                                                                                                        new Response.ErrorListener() {
                                                                                                            @Override
                                                                                                            public void onErrorResponse(VolleyError error) {
                                                                                                                Log.e("FCM Error", "Error sending FCM message: " + error.toString());
                                                                                                            }
                                                                                                        }) {
                                                                                                    @Override
                                                                                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                                                                                        Map<String, String> headers = new HashMap<>();
                                                                                                        // Add authorization token to headers
                                                                                                        headers.put("Authorization", "Bearer " + new_accessToken);
                                                                                                        // Add other headers if needed
                                                                                                        headers.put("Content-Type", "application/json");
                                                                                                        return headers;
                                                                                                    }
                                                                                                };
                                                                                                RequestQueue requestQueue = Volley.newRequestQueue(ManageOrderActivity.this);
                                                                                                requestQueue.add(jsonObjectRequest);

                                                                                            } catch (JSONException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onError(Exception exception) {
                                                                                            // Handle the error
                                                                                            Log.e("AccessToken", "Error: " + exception.getMessage());
                                                                                        }
                                                                                    });

                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                        }
                                    }
                                }
                        );
                        }
                    }
                });
                break;
            case "Finalised":
                optionsLayout0.setVisibility(View.GONE);
                optionsLayout.setVisibility(View.GONE);
                optionsLayout2.setVisibility(View.VISIBLE);
                optionsLayout3.setVisibility(View.GONE);
                optionsLayout4.setVisibility(View.GONE);
                getQrCode.setVisibility(View.GONE);
                dispatchedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            buttonView.setClickable(false);
                            docRef.update("deliveryStatus", "Dispatched");
                            listing.setDeliveryStatus("Dispatched");
                            db.collection("orders").whereEqualTo("listingId", listing.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (!querySnapshot.isEmpty()) {
                                            for (DocumentSnapshot doc : querySnapshot) {
                                                db.collection("orders").document(doc.getId()).update("delivery", "Dispatched");
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case "Dispatched":
                optionsLayout0.setVisibility(View.GONE);
                optionsLayout.setVisibility(View.GONE);
                optionsLayout2.setVisibility(View.GONE);
                optionsLayout3.setVisibility(View.VISIBLE);
                optionsLayout4.setVisibility(View.GONE);
                getQrCode.setVisibility(View.VISIBLE);
                break;
            case "Ready":
                optionsLayout0.setVisibility(View.GONE);
                optionsLayout.setVisibility(View.GONE);
                optionsLayout2.setVisibility(View.GONE);
                optionsLayout3.setVisibility(View.GONE);
                optionsLayout4.setVisibility(View.VISIBLE);
                optionsLayout4.setClickable(false);
                getQrCode.setVisibility(View.GONE);
                db.collection("orders").whereEqualTo("listingId", listing.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot doc : querySnapshot) {
                                    db.collection("orders").document(doc.getId()).update("delivery", "Ready");
                                }
                            }
                        }
                    }
                });

                break;
            default:
                optionsLayout0.setVisibility(View.VISIBLE);
                optionsLayout.setVisibility(View.GONE);
                optionsLayout2.setVisibility(View.GONE);
                optionsLayout3.setVisibility(View.GONE);
                optionsLayout4.setVisibility(View.GONE);
                optionsLayout0.setClickable(false);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent refresh = new Intent(ManageOrderActivity.this, ManageOrderActivity.class);
                refresh.putExtra("listing", listing);
                startActivity(refresh);
            }
        });
    }
    private void captureIntent(String clientSecret) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                String.format("https://api.stripe.com/v1/payment_intents/%s/capture", clientSecret),
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("Success", "Success capture");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ManageOrderActivity.this);
        requestQueue.add(stringRequest);
    }
}
