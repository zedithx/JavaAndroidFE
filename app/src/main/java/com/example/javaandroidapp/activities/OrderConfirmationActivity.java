package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.card.MaterialCardView;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OrderConfirmationActivity extends AppCompatActivity {
    // For Stripes
    PaymentSheet paymentSheet;
    String customerID;
    String ephemeralKey;
    String ClientSecret;
    String SECRET_KEY = "sk_test_51OxodJK24tdo9W186Z7oRocGQytGZsmyXwWyQ6zGSGy8ozksHT1MnwnGheqxlPjKtiwVZZLIH4Eh6VdShSog2WxP00GYwWQoYZ";
    String PUBLISH_KEY= "pk_test_51OxodJK24tdo9W18EForeaCEkq3Bqg0GOKI5VSrhcOLafuA3KQ1p19ewowL6PtT7gN9VtYZCrSyMMZcwKLltIk4c00ph2COvon";
    ImageView listingImageView;
    TextView listingNameTextView;
    TextView orderAmountTextView;
    TextView variantNameTextView;
    LinearLayout imageViewLayout;
    ImageButton backBtn;
    CardView confirmBtn;

    ProgressBar loadingSpinner;
    MaterialCardView orderButton;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        Bundle bundle = getIntent().getExtras();
        System.out.println(bundle);
        MakeOrder orderDetails = (MakeOrder) bundle.getSerializable("new_order");
//        Listing listing = orderDetails.getListing();
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

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Payment was successful
            Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
            Intent Main = new Intent(OrderConfirmationActivity.this, PaymentSuccessActivity.class);
            startActivity(Main);
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
                            // Change loading button to actual button
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
                params.put("amount", "10" + "00");
                params.put("currency", "sgd");
                params.put("automatic_payment_methods[enabled]", "true");
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

