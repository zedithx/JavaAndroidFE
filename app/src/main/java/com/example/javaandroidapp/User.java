package com.example.javaandroidapp;

public class User {
    private int phoneNumber;
    private String address;

    public User() {
        this.phoneNumber = 0;
        this.address = "";
    }

    public User(int phoneNumber, String address) {
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public int getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }
}
