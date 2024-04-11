package com.example.javaandroidapp.activities;

import static com.example.javaandroidapp.activities.ViewProductActivity.df;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.example.javaandroidapp.modals.User;
import com.example.javaandroidapp.utils.OauthToken;
import com.example.javaandroidapp.utils.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaymentSuccessActivity extends AppCompatActivity {
    public Listing listing;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        //initialise notification channel
//        createNotificationChannel();

        // need to retrieve important details through intent
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_success);
        // get the buttons to re-navigate
        CardView viewOrder = findViewById(R.id.viewOrder);

        // get order object from previous page
        Order orderDetails = (Order) getIntent().getSerializableExtra("Order");
        Listing listingDetails = (Listing) getIntent().getSerializableExtra("Listing");
        viewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // navigate to order details page with this order ID
                Intent viewOrderDetail = new Intent(PaymentSuccessActivity.this, ViewOrderDetailsActivity.class);
                viewOrderDetail.putExtra("Order", orderDetails);
                viewOrderDetail.putExtra("Listing", listingDetails);
                startActivity(viewOrderDetail);
            }
        });
        CardView backHome = findViewById(R.id.backHome);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(PaymentSuccessActivity.this, TransitionLandingActivity.class);
                startActivity(Main);
            }
        });

        TextView orderName = findViewById(R.id.name);
        TextView sellerName = findViewById(R.id.seller);
        TextView priceText = findViewById(R.id.price);
        TextView expiryText = findViewById(R.id.expiry);
        TextView minOrder = findViewById(R.id.minorder);
        TextView currentOrder = findViewById(R.id.currentorder);
        ImageView productImg = findViewById(R.id.product_image);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("listings").document(orderDetails.getListingId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        listing = doc.toObject(Listing.class);
                        String nameString = listing.getName();
                        Integer currentOrderVal = listing.getCurrentOrder();
                        Integer minOrderVal = listing.getMinOrder();
                        // If quota is hit, send notification to seller
                        if (currentOrderVal >= minOrderVal){
                            // Obtain the user's device token and specify it as the target recipient
                            // get user to get the device Token
                            Users.getUserFromId(db, listing.getCreatedById(), new CallbackAdapter(){
                                @Override
                                public void getUser(User new_user) {
                                    // Send the notification to the user's device token
                                    String deviceToken = new_user.getUserIdToken();
                                    // Get OauthToken
                                    OauthToken.getAccessToken(getApplicationContext(), new OauthToken.AccessTokenCallback(){
                                        @Override
                                        public void onSuccess(String new_accessToken) {
                                            // Handle the access token
                                            JSONObject messageObject = new JSONObject();
                                            JSONObject notificationObject = new JSONObject();
                                            JSONObject payloadObject = new JSONObject();

                                            try {
                                                // Note: I lost a year of lifespan doing this token stuff
                                                messageObject.put("token", deviceToken);
                                                notificationObject.put("body", String.format("The quota is now %s/%s. Log on to " +
                                                        "the app to confirm your listing now!", currentOrderVal, minOrderVal));
                                                notificationObject.put("title", String.format("Your listing: %s has hit the quota", nameString));
                                                messageObject.put("notification", notificationObject);

                                                payloadObject.put("message", messageObject);

                                                // Create the request
                                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                                                        "https://fcm.googleapis.com/v1/projects/bulkifydb/messages:send",
                                                        payloadObject,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                // Handle successful response
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
                                                RequestQueue requestQueue = Volley.newRequestQueue(PaymentSuccessActivity.this);
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
                            });
                        }
                        if (nameString.length() < 24) {
                            orderName.setText(nameString);
                        } else {
                            String shortened_name = nameString.substring(0, 20) + "...";
                            orderName.setText(shortened_name);
                        }
                        sellerName.setText(listing.getCreatedBy());
                        priceText.setText("S$" + df.format(orderDetails.getItemPrice()));
                        expiryText.setText(listing.getExpiryCountdown());
                        minOrder.setText(String.valueOf(minOrderVal));
                        currentOrder.setText(String.valueOf(currentOrderVal));
                        new ImageLoadTask((listing.getImageList()).get(0), productImg).execute();
                    }
                }
            }
        });
    }
//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is not in the Support Library.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("Bulkify", "BulkifyOrder", NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("Help send notification regarding orders");
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this.
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }
}

