package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.adapters.OrderAdapter;
import com.example.javaandroidapp.modals.Order;
import com.example.javaandroidapp.modals.User;
import com.example.javaandroidapp.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LandingOrdersActivity extends AppCompatActivity {
    private List<Order> orders = new ArrayList<>();
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set page as view
        setContentView(R.layout.landing_order);
        TextView header_name = findViewById(R.id.header_saved);
        header_name.setText("Search Results");
        TextView title_name = findViewById(R.id.title_saved);
        title_name.setText("My Orders");
        ImageView back_arrow = findViewById(R.id.back_arrow);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();

        if (fbUser == null) {
            Intent notSignedIn = new Intent(LandingOrdersActivity.this, LogInActivity.class);
            startActivity(notSignedIn);
        }

        back_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingOrdersActivity.this, TransitionLandingActivity.class);
                startActivity(Main);
            }
        });
        // initialise adapters to bind the listings to
        RecyclerView listingRecyclerOrderView = findViewById(R.id.listingRecyclerOrderView);
        LinearLayoutManager listingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listingRecyclerOrderView.setLayoutManager(listingLayoutManager);
        OrderAdapter adapter_order = new OrderAdapter(orders);
        listingRecyclerOrderView.setAdapter(adapter_order);
        Users.getOrder(db, fbUser, new CallbackAdapter() {
            @Override
            public void getOrders(List<Order> orders_new) {
                orders.clear();
                if (orders_new.size() != 0) {
                    orders.addAll(orders_new);
                    adapter_order.notifyDataSetChanged();
                }
            }
        });
    }
}

