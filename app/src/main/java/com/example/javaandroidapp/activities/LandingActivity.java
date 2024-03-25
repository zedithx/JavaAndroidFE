package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.objects.CategoryModel;
import com.example.javaandroidapp.utils.Categories;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.utils.Listings;
import com.example.javaandroidapp.R;
import com.example.javaandroidapp.utils.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.*;

public class LandingActivity extends AppCompatActivity {
    private List<CategoryModel> categories = new ArrayList<>();
    private List<Listing> listings = new ArrayList<>();

    private QuerySnapshot listing_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // firestore items
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        if (fbUser == null) {
            Intent notSignedIn = new Intent(LandingActivity.this, LogInActivity.class);
            startActivity(notSignedIn);
        }
        // set landing page as view
        setContentView(R.layout.landing);
        // category horizontal carousel
        RecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);
        // Listing vertical carousel
        RecyclerView listingRecyclerView = findViewById(R.id.listingRecyclerView);
        LinearLayoutManager listingLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listingRecyclerView.setLayoutManager(listingLayoutManager);
        // Get all categories from firestore
        // Get name view to edit
//        TextView username = findViewById(R.id.username);
        // Retrieve user's name
//        Users.getName(db, fbUser, new CallbackAdapter() {
//            @Override
//            public void getResult(String result) {
//                if (!result.equals("")) {
//                    username.setText(String.format("Hi, %s!", result));
//                } else {
//                    username.setText("Hi, User!");
//                }
//            }
//        });
        // Get the profile button
        LinearLayout profile_button = findViewById(R.id.avatar);
        profile_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingActivity.this, MenuActivity.class);
                Main.putExtra("User", fbUser);
                startActivity(Main);
            }
        });
        // Get the order and saved buttons
        LinearLayout saved_button = findViewById(R.id.saved_button);
        saved_button.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v) {
                 Intent Main = new Intent(LandingActivity.this, LandingSavedActivity.class);
                 Main.putExtra("User", fbUser);
                 startActivity(Main);
             }
        });
        LinearLayout order_button = findViewById(R.id.order_button);
        order_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO - change back after testing
                Intent Main = new Intent(LandingActivity.this, LandingOrdersActivity.class);
                startActivity(Main);
            }
        });
        ListingAdapter adapter_listing = new ListingAdapter(listings);
        adapter_listing.setOnItemClickListener(new ListingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Listing data) {
                // Handle item click, e.g., start a new activity
                Intent intent = new Intent(LandingActivity.this, ViewProductActivity.class);
                intent.putExtra("listing", data);
                startActivity(intent);
            }
        });
        listingRecyclerView.setAdapter(adapter_listing);
        // Retrieve all listings
        Listings.getAllListings(db, "All", new CallbackAdapter() {
            @Override
            public void getList(List<Listing> listings_new) {
                if (listings_new.size() != 0) {
                    listings.addAll(listings_new);
                }
                adapter_listing.notifyDataSetChanged();
            }
        });

        CategoryAdapter adapter = new CategoryAdapter(categories, listings, db, adapter_listing);
        Categories.getCategorySnapshot(db, new CallbackAdapter() {
            @Override
            public void getCategory(List<CategoryModel> categories_new) {
                if (categories_new.size() != 0) {
                    categories.addAll(categories_new);
                    categoryRecyclerView.setAdapter(adapter);
                }
            }
        });
    }
}

class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder>{
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
    public ListingAdapter.ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listings_landing, parent, false);
        return new ListingAdapter.ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingAdapter.ListingViewHolder holder, int position) {
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
            nameTextView.setText(listing_name);
            minorderTextView.setText(listingMinOrder.toString());
            currentorderTextView.setText(listingCurrentOrder.toString());
            expiryTextView.setText(listingExpiryCountdown);
        }
    }
}

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
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
