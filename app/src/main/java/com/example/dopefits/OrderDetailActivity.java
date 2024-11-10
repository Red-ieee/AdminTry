package com.example.dopefits;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView customerNameTextView, orderIdTextView, orderItemTextView, orderDateTextView, priceTextView, paymentTypeTextView, statusTextView;
    private Button deliverButton, backButton;
    private DatabaseReference completedOrderRef, orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Get data from the Intent
        String customerName = getIntent().getStringExtra("customerName");
        String userId = getIntent().getStringExtra("userId");
        String orderId = getIntent().getStringExtra("orderId");
        String orderItem = getIntent().getStringExtra("orderItem");
        String orderDate = getIntent().getStringExtra("orderDate");
        String price = getIntent().getStringExtra("orderTotal");
        String paymentType = getIntent().getStringExtra("paymentType");
        String status = getIntent().getStringExtra("status");

        Log.d("OrderDetailActivity", "Received price: " + price);

        // Initialize views
        customerNameTextView = findViewById(R.id.customerNameTextView);
        orderIdTextView = findViewById(R.id.orderIdTextView);
        orderItemTextView = findViewById(R.id.orderItemTextView);
        orderDateTextView = findViewById(R.id.orderDateTextView);
        priceTextView = findViewById(R.id.priceTextView);
        paymentTypeTextView = findViewById(R.id.paymentTypeTextView);
        statusTextView = findViewById(R.id.statusTextView);
        backButton = findViewById(R.id.backButton);
        deliverButton = findViewById(R.id.deliverButton);

        // Set data to views
        customerNameTextView.setText(customerName);
        orderIdTextView.setText("Order ID: " + orderId);
        orderItemTextView.setText("Order Item: " + orderItem);
        orderDateTextView.setText("Date of Order: " + orderDate);
        paymentTypeTextView.setText("Payment Type: " + paymentType);
        statusTextView.setText("Status: " + status);

        // Set price data
        if (price != null && !price.isEmpty()) {
            try {
                double priceInDollars = Double.parseDouble(price) / 100.0;
                String formattedPrice = "Total Amount: ₱" + String.format("%.2f", priceInDollars);
                priceTextView.setText(formattedPrice);
            } catch (NumberFormatException e) {
                Log.e("OrderDetailActivity", "Error parsing price: " + price);
                priceTextView.setText("Invalid Price");
            }
        } else {
            priceTextView.setText("Price not available");
        }

        // Check for null values before accessing database
        if (userId != null && orderId != null) {
            completedOrderRef = FirebaseDatabase.getInstance().getReference("completed_orders")
                    .child(userId).child(orderId).child("orderStatus");

            orderRef = FirebaseDatabase.getInstance().getReference("orders")
                    .child(userId).child(orderId).child("orderStatus");

        } else {
            Log.e("OrderDetailActivity", "userId or orderId is null. Cannot construct database reference.");
            Toast.makeText(this, "Order details are incomplete.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle back button click
        backButton.setOnClickListener(v -> finish());

        // Handle deliver button click
        deliverButton.setOnClickListener(v -> {
            if (completedOrderRef != null && orderRef != null) {
                // Update both the completed orders and the regular orders
                String statusToDeliver = "To Deliver";

                // Update the order status in both places
                completedOrderRef.setValue(statusToDeliver).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderRef.setValue(statusToDeliver).addOnCompleteListener(orderTask -> {
                            if (orderTask.isSuccessful()) {
                                statusTextView.setText("Status: To Deliver");
                                Toast.makeText(OrderDetailActivity.this,"Order status updated to 'To Deliver'", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OrderDetailActivity.this, "Failed to update status in 'orders'.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(OrderDetailActivity.this, "Failed to update status in 'completed_orders'.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String price = getIntent().getStringExtra("orderTotal");

        if (price != null && !price.isEmpty()) {
            try {
                double priceInDollars = Double.parseDouble(price) / 100.0;
                String formattedPrice = "Total Amount: ₱" + String.format("%.2f", priceInDollars);
                priceTextView.setText(formattedPrice);
            } catch (NumberFormatException e) {
                Log.e("OrderDetailActivity", "Error parsing price: " + price);
                priceTextView.setText("Invalid Price");
            }
        } else {
            priceTextView.setText("Price not available");
        }
    }
}
