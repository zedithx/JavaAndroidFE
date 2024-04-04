package com.example.javaandroidapp.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.modals.CategoryModel;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.utils.Listings;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static List<CategoryModel> categories;
    private static List<Listing> listings;

    private static FirebaseFirestore db;

    private static ListingAdapter adapter_listing;

    public static List<CategoryModel> getCategories() {
        return categories;
    }

    public CategoryAdapter(List<CategoryModel> categories, List<Listing> listings, FirebaseFirestore db, ListingAdapter adapter_listing) {
        this.categories = categories;
        this.listings = listings;
        this.db = db;
        this.adapter_listing = adapter_listing;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
    public static CategoryModel selectedCategory;
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryTextView;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedCategory != null) {
                        selectedCategory.setSelected(false);
                    }
                    else {
                        categories.get(0).setSelected(false);
                    }
                    Log.d("categories", "category:" + categories);
                    // Get the clicked category
                    CategoryModel clickedCategory = categories.get(getAdapterPosition());
                    Listings.getAllListings(db, clickedCategory.getCategoryName(), new CallbackAdapter() {
                        @Override
                        public void getList(List<Listing> listings_new) {
                            listings.clear();
                            if (listings_new.size() != 0) {
                                listings.addAll(listings_new);
                            }
                            adapter_listing.notifyDataSetChanged();
                        }
                    });
                    // Update the selected category
                    clickedCategory.setSelected(true);

                    selectedCategory = clickedCategory;
                    // Notify the adapter about the data change
                    notifyDataSetChanged();
                }
            });
        }


        public void bind(CategoryModel category) {
            // Bind data to the views in the item layout
            categoryTextView.setText(category.getCategoryName());
            if (category.isSelected()) {
                categoryTextView.setBackgroundResource(R.drawable.categorybox_red);
                categoryTextView.setTextColor(Color.WHITE);// Change to selected color
            }
            else {
                categoryTextView.setBackgroundResource(R.drawable.categorybox_gray);
                categoryTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.darkgray));
            }
        }
    }
}
