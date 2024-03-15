package com.example.javaandroidapp;

public class User {
    private String name;
    private int phoneNumber;
    private String address;

    public User() {
        this.phoneNumber = 0;
        this.name = "Default user";
        this.address = "";
    }

    public User(int phoneNumber, String address, String name) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public int getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() { return this.name; }
}
