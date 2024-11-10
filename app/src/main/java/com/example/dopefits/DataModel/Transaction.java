package com.example.dopefits.DataModel;

public class Transaction {
    private String itemName;
    private String customerName;
    private double price;
    private String paymentType; // New field for payment type

    public Transaction(String itemName, String customerName, double price, String paymentType) {
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

    public double getPrice() {
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

    public void setPrice(double price) {
        this.price = price;
    }

}
