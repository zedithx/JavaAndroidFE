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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class LandingActivity extends AppCompatActivity {
    //testing btn to redirect to pdt page
    public Button testBtn;

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

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);
        RecyclerView categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager);

        List<CategoryModel> categories = new ArrayList<>();
        categories.add(new CategoryModel("All", true));
        categories.add(new CategoryModel("Popular", false));
        categories.add(new CategoryModel("Electronics", false));
        categories.add(new CategoryModel("Consumables", false));
        categories.add(new CategoryModel("Clothes", false));
        categories.add(new CategoryModel("Bags", false));

        CategoryAdapter adapter = new CategoryAdapter(categories);
        categoryRecyclerView.setAdapter(adapter);
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
    private List<LandingActivity.CategoryModel> categories;

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

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO - Switch fragments based on what they click
                }
            });
        }


        public void bind(LandingActivity.CategoryModel category) {
            // Bind data to the views in the item layout
            categoryTextView.setText(category.getCategoryName());
            Log.d("Category", "test" + category.isSelected());
            if (category.isSelected()) {
                categoryTextView.setBackgroundResource(R.drawable.categorybox_red);
                categoryTextView.setTextColor(Color.WHITE);// Change to selected color
            }
        }
    }
}
//