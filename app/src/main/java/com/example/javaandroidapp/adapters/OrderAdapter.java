package com.example.javaandroidapp.adapters;



import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.activities.LandingActivity;
import com.example.javaandroidapp.activities.LandingOrdersActivity;
import com.example.javaandroidapp.activities.ViewOrderDetailsActivity;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{
    static DecimalFormat df = new DecimalFormat("#.00");
    private List<Order> orders;
    public interface OnItemClickListener {
        void onItemClick(Order data);
    }
    public void setOnItemClickListener(OrderAdapter.OnItemClickListener listener) {
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
                // change
//                    clickListener.onItemClick(data);
                    Context context = view.getContext();
                    Intent Main = new Intent(context, ViewOrderDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Order", data);;
                    Main.putExtra("Order", data);
                    context.startActivity(Main);

//                    Main.putExtra("Listing", listing);

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
            TextView orderStatusView = itemView.findViewById(R.id.order_status);
            TextView orderPaidAmountView = itemView.findViewById(R.id.order_paid_amount);
            TextView currentOrderView = itemView.findViewById(R.id.currentorder);
            TextView minorderView = itemView.findViewById(R.id.minorder);
            // Bind data to the views in the item layout
            String orderDelivery = order.getDelivery();
            order.getListing(new CallbackAdapter(){
                @Override
                public void getOrderList(Listing listing) {
                    if (listing != null) {
                        orderNameView.setText(listing.getName());
                        Glide.with(orderView).load(listing.getImageList().get(0)).into(productImageView);
                        currentOrderView.setText(String.valueOf(listing.getCurrentOrder()));
                        minorderView.setText(String.valueOf(listing.getMinOrder()));
                    }
                }
            });
            Integer orderQuantity = order.getQuantity();
            String orderVariant = order.getVariant();
            Double orderPaidAmount = order.getPaidAmount();
            orderVariantView.setText(String.format("%s x%s",orderVariant, orderQuantity));
            orderStatusView.setText(orderDelivery);
            switch(orderDelivery){
                case "Unfulfilled":
                    orderStatusView.setTextColor(Color.RED);
                    break;
                case "Ready":
                    orderStatusView.setTextColor(Color.rgb(0, 150, 0));
                    orderStatusView.setTypeface(orderStatusView.getTypeface(), Typeface.BOLD);
            }
            orderPaidAmountView.setText(String.format("$%s", df.format(orderPaidAmount)));
        }
    }
}
