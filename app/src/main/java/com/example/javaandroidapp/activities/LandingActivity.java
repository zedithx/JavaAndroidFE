package com.example.javaandroidapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    //testing btn to redirect to pdt page
    public Button testBtn;
    private List<CategoryModel> categories = new ArrayList<>();
    private List<Listing> listings = new ArrayList<>();

    private QuerySnapshot listing_items;
    public static class CategoryModel {
        private String categoryName;
        private boolean isSelected;

        public CategoryModel(String categoryName, boolean isSelected) {
            this.categoryName = categoryName;
            this.isSelected = isSelected;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
        @NonNull
        @Override
        public String toString() {
            return "CategoryModel{" +
                    "categoryName='" + categoryName + '\'' +
                    ", isSelected=" + isSelected +
                    '}';
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // firestore items
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
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
        Query categories_query = Categories.getCategorySnapshot(db);
        // Get name view to edit
        TextView username = findViewById(R.id.username);
        // Retrieve user's name
        Users.getName(db, fbUser, new CallbackAdapter() {
            @Override
            public void getResult(String result) {
                if (!result.equals("")) {
                    username.setText(String.format("Hi, %s!", result));
                } else {
                    username.setText("Hi, User!");
                }
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
                Intent Main = new Intent(LandingActivity.this, LandingOrdersActivity.class);
                startActivity(Main);
            }
        });
        ListingAdapter adapter_listing = new ListingAdapter(listings);
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
        categories_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    categories.clear();
                    categories.add(new CategoryModel("All", true));
                    categories.add(new CategoryModel("Popular", false));
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        categories.add(new CategoryModel(document.getData().get("name").toString(), false));
                    }
                    categoryRecyclerView.setAdapter(adapter);
                }
            }
        });
    }
}

class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {
    private List<Listing> listings;

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
        holder.bind(listings.get(position));
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
            listingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Notify the adapter about the data change
//                notifyDataSetChanged();
                    // TODO - direct to product page
                }
            });
        }

        public void bind(Listing listing) {
            TextView priceTextView = itemView.findViewById(R.id.price);
            TextView nameTextView = itemView.findViewById(R.id.name);
            TextView minorderTextView = itemView.findViewById(R.id.minorder);
            TextView currentorderTextView = itemView.findViewById(R.id.currentorder);
            ImageView productImageView = itemView.findViewById(R.id.product_image);
            TextView expiryTextView = itemView.findViewById(R.id.date);
            // Bind data to the views in the item layout
            String listing_price = listing.getPrice();
            String listing_name = listing.getName();
            String listing_minorder = listing.getMinOrder();
            String listing_currentorder = listing.getCurrentOrder();
            String listing_expirydate = listing.getExpiryCountdown();
            Glide.with(listingView).load(listing.getImage()).into(productImageView);
            priceTextView.setText(String.format("$%s", listing_price));
            nameTextView.setText(listing_name);
            minorderTextView.setText(listing_minorder);
            currentorderTextView.setText(listing_currentorder);
            expiryTextView.setText(listing_expirydate);
        }
    }
}

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static List<LandingActivity.CategoryModel> categories;
    private static List<Listing> listings;

    private static FirebaseFirestore db;

    private static ListingAdapter adapter_listing;

    public static List<LandingActivity.CategoryModel> getCategories() {
        return categories;
    }

    public CategoryAdapter(List<LandingActivity.CategoryModel> categories, List<Listing> listings, FirebaseFirestore db, ListingAdapter adapter_listing) {
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
    public static LandingActivity.CategoryModel selectedCategory;
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
                    LandingActivity.CategoryModel clickedCategory = categories.get(getAdapterPosition());
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
                    // TODO - Switch fragments based on what they click
                }
            });
        }


        public void bind(LandingActivity.CategoryModel category) {
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
