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
import com.example.javaandroidapp.objects.Listing;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {
    private List<Listing> listings;

    public interface OnItemClickListener {
        void onItemClick(Listing data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    private OnItemClickListener clickListener;


    //constructor
    public ListingAdapter(List<Listing> listings) {
        this.listings = listings;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listings_landing, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        final Listing data = listings.get(position);
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
        return listings.size();
    }

    class ListingViewHolder extends RecyclerView.ViewHolder {
        private View listingView;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            listingView = itemView.findViewById(R.id.listing);
        }

        public void bind(Listing listing) {
            TextView priceTextView = itemView.findViewById(R.id.price);
            TextView nameTextView = itemView.findViewById(R.id.name);
            TextView minorderTextView = itemView.findViewById(R.id.minorder);
            TextView currentorderTextView = itemView.findViewById(R.id.currentorder);
            ImageView productImageView = itemView.findViewById(R.id.product_image);
            TextView expiryTextView = itemView.findViewById(R.id.date);
            // Bind data to the views in the item layout
            Double listing_price = listing.getPrice();
            String listing_name = listing.getName();
            Integer listingMinOrder = listing.getMinOrder();
            Integer listingCurrentOrder = listing.getCurrentOrder();
            String listingExpiryCountdown = listing.getExpiryCountdown();
            Glide.with(listingView).load(listing.getImageList().get(0)).into(productImageView);
            priceTextView.setText(String.format("$%s", listing_price.toString()));
            if (listing_name.length() < 24) {
                nameTextView.setText(listing_name);
            } else {
                String shortened_name = listing_name.substring(0, 20) + "...";
                nameTextView.setText(shortened_name);
            }
            minorderTextView.setText(listingMinOrder.toString());
            currentorderTextView.setText(listingCurrentOrder.toString());
            expiryTextView.setText(listingExpiryCountdown);
        }
    }
}
