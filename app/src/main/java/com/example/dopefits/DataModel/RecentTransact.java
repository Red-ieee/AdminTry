package com.example.dopefits.DataModel;

public class RecentTransact {
    private String itemName;
    private String customerName;
    private String price;
    private String paymentType; // New field for payment type

    public RecentTransact() {
        // Default constructor required for Firebase
    }

    public RecentTransact(String itemName, String customerName, String price, String paymentType) {
        this.itemName = itemName;
        this.customerName = customerName;
        this.price = price;
        this.paymentType = paymentType;
    }

    // Getters
    public String getItemName() {
        return itemName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPrice() {
        return price;
    }

    public String getPaymentType() {
        return paymentType;
    } // New getter for payment type

    // Setters, if needed
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}


