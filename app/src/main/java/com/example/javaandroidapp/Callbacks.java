package com.example.javaandroidapp;

import java.util.List;

public interface Callbacks {
    void onResult(boolean isSuccess);
    void getResult(String result);
    void getList(List<Listing> listings);
}
