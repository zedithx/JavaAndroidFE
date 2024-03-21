package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.objects.Order;
import com.example.javaandroidapp.utils.Orders;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LandingOrdersActivity extends AppCompatActivity {
    private List<Order> orders = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set page as view
        setContentView(R.layout.landing_order);
        TextView header_name = findViewById(R.id.header_saved);
        header_name.setText("Orders");
        TextView title_name = findViewById(R.id.title_saved);
        title_name.setText("My Orders");
        ImageView back_arrow = findViewById(R.id.back_arrow);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();

        back_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingOrdersActivity.this, LandingActivity.class);
                startActivity(Main);
            }
        });
        Orders.getOrdersBasedOnUser(db, fbUser, new CallbackAdapter() {
            @Override
            public void getOrder(List<Order> orders_new) {
                if (orders_new.size() != 0) {
                    System.out.println(orders_new);
                    orders.addAll(orders_new);
                }
            }
        });
    }
}
