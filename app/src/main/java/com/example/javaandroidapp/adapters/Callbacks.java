package com.example.javaandroidapp.adapters;

import com.example.javaandroidapp.objects.Listing;

import java.util.List;

public interface Callbacks {
    void onResult(boolean isSuccess);
    void getResult(String result);
    void getList(List<Listing> listings);
}
