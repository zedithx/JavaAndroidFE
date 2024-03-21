package com.example.javaandroidapp.adapters;

import com.example.javaandroidapp.activities.LandingActivity;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.objects.Order;

import java.util.List;

public interface Callbacks {
    void onResult(boolean isSuccess);
    void getResult(String result);
    void getList(List<Listing> listings);
    void getOrder(List<Order> orders);
    void getCategory(List<LandingActivity.CategoryModel> categories);
}
