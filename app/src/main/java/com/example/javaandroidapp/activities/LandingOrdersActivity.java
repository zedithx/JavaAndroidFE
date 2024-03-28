package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.objects.Order;
import com.example.javaandroidapp.objects.User;
import com.example.javaandroidapp.utils.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
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
        header_name.setText("Orders");
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
                Intent Main = new Intent(LandingOrdersActivity.this, LandingActivity.class);
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
            public void getOrder(List<Order> orders_new) {
                orders.clear();
                if (orders_new.size() != 0) {
                    orders.addAll(orders_new);
                    adapter_order.notifyDataSetChanged();
                }
            }
        });
    }
}

class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{
    private List<Order> orders;
    public interface OnItemClickListener {
        void onItemClick(Order data);
    }
    public void setOnItemClickListener(com.example.javaandroidapp.activities.OrderAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }
    private OnItemClickListener clickListener;


    //constructor
    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }
    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_landing, parent, false);
        return new OrderAdapter.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        final Order data = orders.get(position);
        holder.bind(data);

        // Set click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onItemClick(data);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private View orderView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            //TODO - change to order
            orderView = itemView.findViewById(R.id.order_landing);
        }

        public void bind(Order order) {
            ImageView productImageView = itemView.findViewById(R.id.product_image);
            TextView orderNameView = itemView.findViewById(R.id.order_name);
            TextView orderVariantView = itemView.findViewById(R.id.order_variant);
            TextView orderQuantityView = itemView.findViewById(R.id.order_quantity);
            TextView orderStatusView = itemView.findViewById(R.id.order_status);
            TextView orderPaidAmountView = itemView.findViewById(R.id.order_paid_amount);
            // Bind data to the views in the item layout
            String orderDelivery = order.getDelivery();
            order.getListing(new CallbackAdapter(){
                @Override
                public void getOrderList(Listing listing) {
                    if (listing != null) {
                        orderNameView.setText(listing.getName());
                        Glide.with(orderView).load(listing.getImageList().get(0)).into(productImageView);
                    }
                }
            });
            Integer orderQuantity = order.getQuantity();
            String orderVariant = order.getVariant();
            Double orderPaidAmount = order.getPaidAmount();
            orderVariantView.setText(orderVariant);
            orderQuantityView.setText(String.format("Qty: %s", String.valueOf(orderQuantity)));
            orderStatusView.setText(orderDelivery);
            orderPaidAmountView.setText(String.format("$%s", String.valueOf(orderPaidAmount)));
        }
    }
}
