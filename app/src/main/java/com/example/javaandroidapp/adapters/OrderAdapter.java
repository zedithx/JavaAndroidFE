package com.example.javaandroidapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{
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
            orderVariantView.setText(orderVariant);
            orderQuantityView.setText(String.format("Qty: %s", String.valueOf(orderQuantity)));
            orderStatusView.setText(orderDelivery);
            orderPaidAmountView.setText(String.format("$%s", String.valueOf(orderPaidAmount)));
        }
    }
}
