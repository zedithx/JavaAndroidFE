package com.example.javaandroidapp.adapters;

import com.example.javaandroidapp.modals.CategoryModel;
import com.example.javaandroidapp.modals.Listing;
import com.example.javaandroidapp.modals.Order;
import com.example.javaandroidapp.modals.User;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;

import io.getstream.chat.android.models.Channel;
import io.getstream.chat.java.exceptions.StreamException;

abstract public class CallbackAdapter implements Callbacks{
    @Override
    public void onResult(boolean isSuccess) {

    }

    @Override
    public void getResult(String result) {

    }

    @Override
    public void getList(List<Listing> listings) {

    }

    @Override
    public void getOrders(List<Order> orders) {


    }
    @Override
    public void getOrder(Order order) {


    }
    @Override
    public void getCategory(List<CategoryModel> categories) {

    }

    @Override
    public void getListOfString(List<String> strings) {

    }

    @Override
    public void getArrayListOfString(ArrayList<String> strings) {

    }
    @Override
    public void getOrderList(Listing listing){

    }

    @Override
    public void getUser(User user) throws StreamException{

    }

    @Override
    public void getChannel(Channel channel) {

    }

}
