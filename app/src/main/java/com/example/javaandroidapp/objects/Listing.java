package com.example.javaandroidapp.objects;


import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;

public class Listing implements Serializable {
    private String category;
    private String createdBy;
    private Integer currentOrder;
    private String description;
    private Date expiry;
    private Integer minOrder;
    private String name;
    private Double oldPrice;
    private Double price;
    //TODO - change to variant collection
    private ArrayList<String> variationNames = new ArrayList<>();
    private ArrayList<Double> variationAdditionalPrice = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();

    public Listing() {}

    public Listing(Double price, String name, Integer minOrder, Integer currentOrder, Date expiry, ArrayList<String> imageList,
                   String createdBy, String description, Double oldPrice, String category, ArrayList<String> variationNames, ArrayList<Double> variationAdditionalPrice) {
        this.price = price;
        this.name = name;
        this.minOrder = minOrder;
        this.currentOrder = currentOrder;
        this.expiry = expiry;
        this.imageList = imageList;
        this.description = description;
        this.oldPrice = oldPrice;
        this.createdBy = createdBy;
        this.category = category;
        this.variationNames = variationNames;
        this.variationAdditionalPrice = variationAdditionalPrice;
    }

    public Double getOldPrice() {
        return this.oldPrice;
    }

    public Double getPrice() {
        return this.price;
    }

    public Integer getCurrentOrder() {
        return this.currentOrder;
    }

    public Integer getMinOrder() {
        return this.minOrder;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<String> getImageList() {
        return this.imageList;
    }

    public String getCategory() {
        return this.category;
    }

    public String getCreated_by() {
        return this.createdBy;
    }

    public String getDescription() {
        return this.description;
    }

    public Date getExpiry() {
        return  this.expiry;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
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

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCurrentOrder(Integer currentOrder) {
        this.currentOrder = currentOrder;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public void setMinOrder(Integer minOrder) {
        this.minOrder = minOrder;
    }

    public void setOldPrice(Double oldPrice) {
        this.oldPrice = oldPrice;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public ArrayList<String> getVariationNames() {
        return variationNames;
    }

    public ArrayList<Double> getVariationAdditionalPrice() {
        return variationAdditionalPrice;
    }

    public void setVariationNames(ArrayList<String> variationNames) {
        this.variationNames = variationNames;
    }

    public void setVariationAdditionalPrice(ArrayList<Double> variationAdditionalPrice) {
        this.variationAdditionalPrice = variationAdditionalPrice;
    }
}
