package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.os.Bundle;
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

        back_arrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingOrdersActivity.this, LandingActivity.class);
                startActivity(Main);
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        // initialise adapters to bind the listings to
//        RecyclerView listingRecyclerOrderView = findViewById(R.id.listingRecyclerOrderView);
//        LinearLayoutManager listingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        listingRecyclerOrderView.setLayoutManager(listingLayoutManager);
//        OrderAdapter adapter_listing = new OrderAdapter(orders);
//        listingRecyclerOrderView.setAdapter(adapter_listing);
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


//class OrderAdapter extends RecyclerView.Adapter<com.example.javaandroidapp.activities.OrderAdapter.ListingViewHolder>{
//    private List<Order> orders;
//    public interface OnItemClickListener {
//        void onItemClick(Listing data);
//    }
//    public void setOnItemClickListener(com.example.javaandroidapp.activities.OrderAdapter.OnItemClickListener listener) {
//        this.clickListener = listener;
//    }
//    private com.example.javaandroidapp.activities.ListingAdapter.OnItemClickListener clickListener;
//
//
//    //constructor
//    public OrderAdapter(List<Listing> listings) {
//        this.listings = listings;
//    }
//    @NonNull
//    @Override
//    public com.example.javaandroidapp.activities.OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listings_landing, parent, false);
//        return new com.example.javaandroidapp.activities.OrderAdapter.ListingViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull com.example.javaandroidapp.activities.OrderAdapter.OrderViewHolder holder, int position) {
//        final Order data = orders.get(position);
//        holder.bind(data);
//
//        // Set click listener for the item view
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (clickListener != null) {
//                    clickListener.onItemClick(data);
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return Orders.size();
//    }
//
//    class OrderViewHolder extends RecyclerView.ViewHolder {
//        private View orderView;
//
//        public OrderViewHolder(@NonNull View itemView) {
//            super(itemView);
//            //TODO - change to order
//            orderView = itemView.findViewById(R.id.listing);
//        }
//
//        public void bind(Listing listing) {
//            TextView priceTextView = itemView.findViewById(R.id.price);
//            TextView nameTextView = itemView.findViewById(R.id.name);
//            TextView minorderTextView = itemView.findViewById(R.id.minorder);
//            TextView currentorderTextView = itemView.findViewById(R.id.currentorder);
//            ImageView productImageView = itemView.findViewById(R.id.product_image);
//            TextView expiryTextView = itemView.findViewById(R.id.date);
//            // Bind data to the views in the item layout
//            Double listing_price = listing.getPrice();
//            String listing_name = listing.getName();
//            Integer listing_minorder = listing.getMinorder();
//            Integer listing_currentorder = listing.getCurrentorder();
//            String listing_expirydate = listing.getExpiryCountdown();
//            Glide.with(orderView).load(listing.getImage()).into(productImageView);
//            priceTextView.setText(String.format("$%s", listing_price.toString()));
//            nameTextView.setText(listing_name);
//            minorderTextView.setText(listing_minorder.toString());
//            currentorderTextView.setText(listing_currentorder.toString());
//            expiryTextView.setText(listing_expirydate.toString());
//        }
//    }
//}
