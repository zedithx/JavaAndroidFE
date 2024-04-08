package com.example.javaandroidapp.adapters;

import com.example.javaandroidapp.modals.CategoryModel;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.example.javaandroidapp.modals.User;

import java.util.ArrayList;
import java.util.List;

import io.getstream.chat.android.models.Channel;
import io.getstream.chat.java.exceptions.StreamException;

public interface Callbacks {
    void onResult(boolean isSuccess);
    void getResult(String result);
    void getList(List<Listing> listings);
    void getOrders(List<Order> orders);
    void getOrder(Order order);
    void getCategory(List<CategoryModel> categories);
    void getListOfString(List<String> strings);

    void getArrayListOfString(ArrayList<String> strings);
    void getOrderList(Listing listing);
    void getUser(User user) throws StreamException;
    void getChannel(Channel channel);
}
