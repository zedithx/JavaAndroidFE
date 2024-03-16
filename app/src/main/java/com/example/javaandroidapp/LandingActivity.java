package com.example.javaandroidapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.*;

public class LandingActivity extends AppCompatActivity {
    //testing btn to redirect to pdt page
    public Button testBtn;
    private List<CategoryModel> categories = new ArrayList<>();
    private List<Listing> listings = new ArrayList<>();

    private QuerySnapshot listing_items;
    public static class Listing {
        private String price;
        private String name;
        private String minOrder;
        private String currentOrder;
        private Date expiryDate;

        public Listing(String price, String name, String minOrder, String currentOrder, Date expiryDate) {
            this.price = price;
            this.name = name;
            this.minOrder = minOrder;
            this.currentOrder = currentOrder;
            this.expiryDate = expiryDate;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMinOrder() {
            return minOrder;
        }

        public void setMinOrder(String minOrder) {
            this.minOrder = minOrder;
        }

        public String getCurrentOrder() {
            return currentOrder;
        }

        public void setCurrentOrder(String currentOrder) {
            this.currentOrder = currentOrder;
        }
        public Date getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
        }
    }
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
        // Get user's name
        DocumentReference userNameRef = Users.getName(db, fbUser);
        TextView username = findViewById(R.id.username);
        ListingAdapter adapter_listing = new ListingAdapter(listings);
        listingRecyclerView.setAdapter(adapter_listing);
        // Retrieve all listings
        Query listings_query = Listings.getAllListings(db, "All");
        listings_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Log.d("document", "document:" + document);
                        String listing_price = document.get("price").toString();
                        String listing_name = document.getString("name");
                        String listing_minorder = document.get("minorder").toString();
                        String listing_currentorder = document.get("currentorder").toString();
                        Date listing_expirydate = document.getDate("expiry");
                        Listing listing = new Listing(listing_price, listing_name, listing_minorder,
                                listing_currentorder, listing_expirydate);
                        listings.add(listing);
                        Log.d("listings", "listings:" + listings);
                    }
                    adapter_listing.notifyDataSetChanged();
                }
            }
        });
        userNameRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username.setText(String.format("Hi, %s!", document.getData().get("name").toString()));
                    }
                }
            }
        });

        CategoryAdapter adapter = new CategoryAdapter(categories);
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

        //testing btn to redirect to pdt page
//        testBtn = findViewById(R.id.testButton);
//        testBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent Main = new Intent(LandingActivity.this, ViewProductActivity.class);
//                startActivity(Main);
//            }
//        });
    }
}

class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {
    private List<LandingActivity.Listing> listings;
    //constructor
    public ListingAdapter(List<LandingActivity.Listing> listings) {
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
        //    String listing_price = (String) documentData.get("price");
//    String listing_name = (String) documentData.get("name");
//    String listing_minorder = (String) documentData.get("minorder");
//    String listing_currentorder = (String) documentData.get("currentorder");
//    Date listing_expirydate = (Date) documentData.get("expiry");
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

        public void bind(LandingActivity.Listing listing) {
            TextView priceTextView = itemView.findViewById(R.id.price);
            TextView nameTextView = itemView.findViewById(R.id.name);
            TextView minorderTextView = itemView.findViewById(R.id.minorder);
            TextView currentorderTextView = itemView.findViewById(R.id.currentorder);
//            TextView expiryTextView = itemView.findViewById(R.id.date);
            // Bind data to the views in the item layout
            String listing_price = (String) listing.getPrice();
            String listing_name = (String) listing.getName();
            String listing_minorder = (String) listing.getMinOrder();
            String listing_currentorder = (String) listing.getCurrentOrder();
//            Date listing_expirydate = (Date) listing.getExpiryDate();
            priceTextView.setText(String.format("$%s", listing_price));
            nameTextView.setText(listing_name);
            minorderTextView.setText(listing_minorder);
            currentorderTextView.setText(listing_currentorder);
//            priceTextView.setText(listing_expirydate);
        }
    }
}

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private static List<LandingActivity.CategoryModel> categories;

    public static List<LandingActivity.CategoryModel> getCategories() {
        return categories;
    }

    public CategoryAdapter(List<LandingActivity.CategoryModel> categories) {
        this.categories = categories;
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
