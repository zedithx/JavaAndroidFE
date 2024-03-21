package com.example.javaandroidapp.objects;


import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;

public class Listing implements Serializable {
    private String category;
    private String created_by;
    private Integer currentorder;
    private String description;
    private Date expiry;
    private String image;
    private Integer minorder;
    private String name;
    private Double old_price;
    private Double price;

    public Listing() {}

    public Listing(Double price, String name, Integer minorder, Integer currentorder, Date expiry, String image) {
        this.price = price;
        this.name = name;
        this.minorder = minorder;
        this.currentorder = currentorder;
        this.expiry = expiry;
        this.image = image;
        this.description = "";
        this.old_price = 123.1;
        this.created_by = "asd";
        this.category = "asd";
    }

    public Listing(Double price, String name, Integer minorder, Integer currentorder, Date expiry, String image, String created_by, String description, Double old_price, String category) {
        this.price = price;
        this.name = name;
        this.minorder = minorder;
        this.currentorder = currentorder;
        this.expiry = expiry;
        this.image = image;
        this.description = description;
        this.old_price = old_price;
        this.created_by = created_by;
        this.category = category;
    }

    public Double getOld_price() {
        return this.old_price;
    }

    public Double getPrice() {
        return this.price;
    }

    public Integer getCurrentorder() {
        return this.currentorder;
    }

    public Integer getMinorder() {
        return this.minorder;
    }

    public String getName() {
        return this.name;
    }

    public String getImage() {
        return this.image;
    }

    public String getCategory() {
        return this.category;
    }

    public String getCreated_by() {
        return this.created_by;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getExpiry() {
        return  this.expiry;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public void setCurrentorder(Integer currentorder) {
        this.currentorder = currentorder;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public void setMinorder(Integer minorder) {
        this.minorder = minorder;
    }

    public void setOld_price(Double old_price) {
        this.old_price = old_price;
    }

    public String getExpiryCountdown() {
    // Convert the Date object to LocalDate
    LocalDate localDate = expiry.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

    // Get today's date
    LocalDate today = LocalDate.now();

    // Calculate the period between the two dates
    Period period = Period.between(today, localDate);

    // Return the number of days in the period
    return period.getDays() + " Days Left";
    }
}

//public class Listing implements Serializable {
//    private double price;
//    private String name;
//    private Integer minOrder;
//    private Integer currentOrder;
//    private String image;
//    private Date expiryDate;
//
//    public Listing() {
//
//    }
//
//    public Listing(double price, String name, Integer minOrder, Integer currentOrder, Date expiryDate, String image) {
//        this.price = price;
//        this.name = name;
//        this.minOrder = minOrder;
//        this.currentOrder = currentOrder;
//        this.expiryDate = expiryDate;
//        this.image = image;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Integer getMinOrder() {
//        return minOrder;
//    }
//
//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }
//
//    public void setMinOrder(Integer minOrder) {
//        this.minOrder = minOrder;
//    }
//
//    public Integer getCurrentOrder() {
//        return currentOrder;
//    }
//
//    public void setCurrentOrder(Integer currentOrder) {
//        this.currentOrder = currentOrder;
//    }
//    public Date getExpiryDate() {
//        return expiryDate;
//    }
//
//    public String getExpiryCountdown() {
//        // Convert the Date object to LocalDate
//        LocalDate localDate = expiryDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
//
//        // Get today's date
//        LocalDate today = LocalDate.now();
//
//        // Calculate the period between the two dates
//        Period period = Period.between(today, localDate);
//
//        // Return the number of days in the period
//        return period.getDays() + " Days Left";
//    }
//
//    public void setExpiryDate(Date expiryDate) {
//        this.expiryDate = expiryDate;
//    }
//}