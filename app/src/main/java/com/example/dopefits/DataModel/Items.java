package com.example.dopefits.DataModel;

import java.util.List;

public class Items {
    private int categoryId;
    private int id;
    private List<String> picUrl;
    private String title;
    private int price;
    private int quantity;
    private String size;
    private String brand;
    private String condition;
    private String issue;
    private String description;
    private String firebaseKey;

    // Default constructor
    public Items(int categoryId, List<String> picUrl, String title, int price, int quantity, String size, String brand, String condition, String issue, String description, String firebaseKey) {
        this.categoryId = categoryId;
        this.picUrl = picUrl;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.size = size;
        this.brand = brand;
        this.condition = condition;
        this.issue = issue;
        this.description = description;
        this.firebaseKey = firebaseKey;
    }

    // Constructor with essential fields
    public Items(int categoryId, List<String> picUrl, String title) {
        this.categoryId = categoryId;
        this.picUrl = picUrl;
        this.title = title;
    }

    // Getters
    public int getCategoryId() {
        return categoryId;
    }

    public int getId() {
        return id;
    }

    public List<String> getPicUrl() {
        return picUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSize() {
        return size;
    }

    public String getBrand() {
        return brand;
    }

    public String getCondition() {
        return condition;
    }

    public String getIssue() {
        return issue;
    }

    public String getDescription() {
        return description;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    // Setters
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPicUrl(List<String> picUrl) {
        this.picUrl = picUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }


}
