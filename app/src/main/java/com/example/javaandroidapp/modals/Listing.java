package com.example.javaandroidapp.modals;


import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Listing implements Serializable{
    @DocumentId
    private String uid;
    private String category;
    private String createdBy;
    private Integer currentOrder;
    private String description;
    private Date expiry;
    private Integer minOrder;
    private String name;
    private double oldPrice;
    private double price;
    //TODO - change to variant collection
    private ArrayList<String> variationNames = new ArrayList<>();
    private ArrayList<Double> variationAdditionalPrice = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();
    private ArrayList<DocumentReference> orders = new ArrayList<>();

    public Listing() {}

    public Listing(Double price, String name, Integer minOrder, Date expiry, ArrayList<String> imageList,
                   String createdBy, String description, Double oldPrice, String category, ArrayList<String> variationNames, ArrayList<Double> variationAdditionalPrice) {
        this.price = price;
        this.name = name;
        this.minOrder = minOrder;
        this.currentOrder = 0;
        this.expiry = expiry;
        this.imageList = imageList;
        this.description = description;
        this.oldPrice = oldPrice;
        this.createdBy = createdBy;
        this.category = category;
        this.variationNames = variationNames;
        this.variationAdditionalPrice = variationAdditionalPrice;
    }

    public String getUid() {
        return uid;
    }
    public Double getOldPrice() {
        return this.oldPrice;
    }

    public double getPrice() {
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

    public String getDescription() {
        return this.description;
    }

    public Date getExpiry() {
        return  this.expiry;
    }

    public String getExpiryCountdown() {
        // Convert the Date object to LocalDate
        LocalDate localDate = expiry.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        // Get today's date
        LocalDate today = LocalDate.now();

        // Calculate the period between the two dates
        Period period = Period.between(today, localDate);
        String yearsLeft = period.getYears() < 1 ? "" : period.getYears() + "y ";
        String monthsLeft = period.getMonths() < 1 ? "" : period.getMonths() + "m ";
        String daysLeft = "";
        if ((period.getDays() < 1 && period.getMonths() < 1 && period.getYears() < 1) || period.getDays() > 1){
            daysLeft = period.getDays() + "d left";
        }else {
            daysLeft = "left";
        }

        // Return the number of days in the period
        return yearsLeft + monthsLeft + daysLeft;
    }

    public String getCreatedBy() {
        return createdBy;
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
    public void setUid(String uid){
        this.uid = uid;
    }
    public static Listing createListingWithDocumentSnapshot(DocumentSnapshot doc){
        return doc.toObject(Listing.class);
    }
}
