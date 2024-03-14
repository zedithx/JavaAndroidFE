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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.*;

public class LandingActivity extends AppCompatActivity {
    //testing btn to redirect to pdt page
    public Button testBtn;
    private List<CategoryModel> categories = new ArrayList<>();

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        setContentView(R.layout.landing);
        RecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager);
        Query categories_query = Categories.getCategorySnapshot(db);
        CategoryAdapter adapter = new CategoryAdapter(categories);
        categories_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    categories.clear();
                    categories.add(new CategoryModel("All", true));
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        categories.add(new CategoryModel(document.getData().get("name").toString(), false));
                    }
                    categoryRecyclerView.setAdapter(adapter);
                }
            }
        });
//        List<CategoryModel> categories = new ArrayList<>();
//        categories.add(new CategoryModel("All", true));
//        categories.add(new CategoryModel("Popular", false));
//        categories.add(new CategoryModel("Electronics", false));
//        categories.add(new CategoryModel("Consumables", false));
//        categories.add(new CategoryModel("Clothes", false));
//        categories.add(new CategoryModel("Bags", false));
        //testing btn to redirect to pdt page
        testBtn = findViewById(R.id.testButton);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Main = new Intent(LandingActivity.this, ViewProductActivity.class);
                startActivity(Main);
            }
        });
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
//    public static LandingActivity.CategoryModel selectedCategory = categories.get(0);
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
