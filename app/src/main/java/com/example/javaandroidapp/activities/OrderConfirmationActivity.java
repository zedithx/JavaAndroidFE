package com.example.javaandroidapp.activities;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.example.javaandroidapp.utils.Listings;
import com.example.javaandroidapp.utils.Orders;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderConfirmationActivity extends AppCompatActivity {
    // For Stripes
    PaymentSheet paymentSheet;
    String customerID;
    String ephemeralKey;
    String ClientSecret;

    String SECRET_KEY;
    String PUBLISH_KEY;
    ImageView listingImageView;
    TextView listingNameTextView;
    TextView orderAmountTextView;
    TextView variantNameTextView;
    LinearLayout imageViewLayout;
    ImageButton backBtn;
    CardView confirmBtn;
    ApplicationInfo applicationInfo;
    ProgressBar loadingSpinner;
    MaterialCardView orderButton;
    FirebaseFirestore db;
    Order orderDetails;
    String listingUID;
    FirebaseUser fbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        fbUser = mAuth.getCurrentUser();
        // Get SECRET KEY
        try {
            applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (applicationInfo != null){
            SECRET_KEY = applicationInfo.metaData.getString("secretKey");
            PUBLISH_KEY = applicationInfo.metaData.getString("publishKey");
        }
        // get order object from previous page
        orderDetails = (Order) getIntent().getSerializableExtra("Order");
        listingUID = (String) getIntent().getSerializableExtra("ListingUid");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirmation);
        //transition from loading to order after ClientSecret has been obtained
        loadingSpinner = findViewById(R.id.loadingSpinner);
        orderButton = findViewById(R.id.confirmButton);
        //Stripes config
        PaymentConfiguration.init(this, PUBLISH_KEY);
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });
        // Make api request that sequentially creates customer id, ephemeral key, and subsequently clientsecret
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
                            getEphemeralKey(customerID);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
        };

        RequestQueue requestQueue = Volley.newRequestQueue(OrderConfirmationActivity.this);
        requestQueue.add(stringRequest);
//
//
//
//        listingNameTextView = findViewById(R.id.productName);
//        imageViewLayout = findViewById(R.id.imageViewLayout);
//        listingImageView = findViewById(R.id.listingImage);
//        variantNameTextView = findViewById(R.id.variant_name);
//        orderAmountTextView = findViewById(R.id.variant_amt);
//
//        listingNameTextView.setText(listing.getName());
//        Glide.with(imageViewLayout).load(listing.getImageList().get(0)).into(listingImageView);
//        variantNameTextView.setText(orderDetails.getVariantName());
//        orderAmountTextView.setText("" + orderDetails.getAmount());
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirmBtn = findViewById(R.id.confirmButton);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger PaymentSheet
                PaymentFlow();
            }
        });
    }

    // All stripes functions

    // This is the callback for success on payment
    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            //add order object to order collection with userid
            Orders.createOrder(db, orderDetails, fbUser, listingUID, new CallbackAdapter() {
                @Override
                public void getResult(String orderId) {
                    if (orderId != null) {
                        //Store client secret
                        Orders.storeClientSecret(db, orderId, ClientSecret, new CallbackAdapter() {
                            @Override
                            public void getResult(String orderId) {
                                //add reference to order on Listing increment number of orders
                                Orders.listingReference(db, listingUID, orderId, new CallbackAdapter(){
                                    @Override
                                    public void getResult(String orderId){
                                        //add order to user list
                                        Orders.userReference(db, fbUser, orderId, new CallbackAdapter(){
                                            @Override
                                            public void onResult(boolean isSuccess){
                                                // Notify user
                                                Toast.makeText(getApplicationContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                                                Intent Main = new Intent(OrderConfirmationActivity.this, PaymentSuccessActivity.class);
                                                startActivity(Main);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            });
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            // Payment failed
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            // Payment was canceled
            Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void getEphemeralKey(String customerID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ephemeralKey =object.getString("id");
                            getClientSecret(customerID, ephemeralKey);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                header.put("Stripe-Version", "2023-10-16");
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(OrderConfirmationActivity.this);
        requestQueue.add(stringRequest);
    }
    private void getClientSecret(String customerID, String ephemeralKey) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret=object.getString("client_secret");
                            // At this point we have all variables needed to proceed with paymentSheetIntent
                            loadingSpinner.setVisibility(View.GONE);
                            orderButton.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", String.format("%.0f",orderDetails.getPaidAmount() * 100));
                params.put("currency", "sgd");
                params.put("automatic_payment_methods[enabled]", "true");
                //changes to withold. Need to store all clientsecrets to be used to capture later on
                params.put("capture_method", "manual");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(OrderConfirmationActivity.this);
        requestQueue.add(stringRequest);
    }

    private void PaymentFlow() {
        // Needs to wait for client secret first
        paymentSheet.presentWithPaymentIntent(ClientSecret,
                new PaymentSheet.Configuration("Bulkify")
        );
    }
}

