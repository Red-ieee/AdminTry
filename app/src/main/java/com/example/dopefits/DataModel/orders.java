package com.example.dopefits.DataModel;

public class orders {
    private String orderId;
    private String productName;
    private String orderTotal;
    private String orderStatus;
    private String orderDate;
    private String userId;
    private String paymentType;
    private String customerName;

    public orders() {
    }

    public orders(String orderId, String productName, String customerName, String orderTotal, String orderStatus, String orderDate, String userId, String paymentType) {
        this.orderId = orderId;
        this.productName = productName;
        this.customerName = customerName;
        this.orderTotal = orderTotal;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.userId = userId;
        this.paymentType = paymentType;
    }



    // Getters
    public String getOrderId() { return orderId; }
    public String getProductName() { return productName; }
    public String getOrderTotal() { return orderTotal; }
    public String getOrderStatus() { return orderStatus; }
    public String getOrderDate() { return orderDate; }
    public String getUserId() { return userId; }
    public String getPaymentType() { return paymentType; }
    public String getCustomerName() { return customerName; }

    // Setters
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setOrderTotal(String orderTotal) { this.orderTotal = orderTotal; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}
