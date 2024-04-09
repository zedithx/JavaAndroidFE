package com.example.javaandroidapp.activities;

import static com.example.javaandroidapp.activities.ViewProductActivity.df;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PaymentSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
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
                        orderName.setText(doc.getString("name"));
                        sellerName.setText(doc.getString("createdBy"));
                        priceText.setText("S$" + df.format(orderDetails.getItemPrice()));
                        expiryText.setText(doc.getString("expiryCountdown"));
                        minOrder.setText(""+(doc.getDouble("minOrder").intValue()));
                        currentOrder.setText(""+(doc.getDouble("currentOrder")).intValue());
                        new ImageLoadTask(((ArrayList<String>) doc.get("imageList")).get(0), productImg).execute();
                    }
                }
            }
        });
    }
}
