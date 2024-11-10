package com.example.dopefits.Fragments;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dopefits.AdapterClass.PendingOrderAdapter;
import com.example.dopefits.DataModel.orders;
import com.example.dopefits.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PendingOrderFragment extends Fragment implements PendingOrderAdapter.OnOrderAcceptedListener {
    private RecyclerView recyclerViewPendingOrders;
    private PendingOrderAdapter adapter;
    private DatabaseReference ordersRef;
    private TextView noPendingOrderMessage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_order, container, false);

        noPendingOrderMessage = view.findViewById(R.id.noPendingOrderMessage);
        recyclerViewPendingOrders = view.findViewById(R.id.recyclerViewPendingOrder);
        recyclerViewPendingOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        fetchPendingOrders();

        return view;
    }

    private void fetchPendingOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<orders> orderList = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey(); // Get the user ID
                    Log.d("PendingOrderFragment", "User ID: " + userId);

                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                        try {
                            orders order = orderSnapshot.getValue(orders.class);
                            if (order != null && "Order Placed".equals(order.getOrderStatus())) {
                                order.setUserId(userId);
                                orderList.add(order);
                                Log.d("PendingOrderFragment", "Order added: " + order.getOrderId());
                            } else {
                                Log.w("PendingOrderFragment", "Order data was null or not pending for snapshot: " + orderSnapshot.getKey());
                            }
                        } catch (Exception e) {
                            Log.e("PendingOrderFragment", "Error parsing order: " + e.getMessage(), e);
                        }
                    }
                }

                if (!orderList.isEmpty()) {
                    noPendingOrderMessage.setVisibility(View.GONE);  // Hide message if there are orders
                    recyclerViewPendingOrders.setVisibility(View.VISIBLE);  // Show RecyclerView
                    adapter = new PendingOrderAdapter(orderList, PendingOrderFragment.this);
                    recyclerViewPendingOrders.setAdapter(adapter);
                } else {
                    noPendingOrderMessage.setVisibility(View.VISIBLE);  // Show message if no orders
                    recyclerViewPendingOrders.setVisibility(View.GONE);  // Hide RecyclerView
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PendingOrderFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onOrderAccepted(orders order) {
        if (order != null && order.getOrderId() != null && order.getUserId() != null) {
            ordersRef.child(order.getUserId()).child(order.getOrderId()).child("orderStatus").setValue("To ship")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            moveOrderToCompleted(order);
                            adapter.removeOrder(order);
                        } else {
                            Log.e("PendingOrderFragment", "Failed to update order status");
                        }
                    });
        } else {
            Log.w("PendingOrderFragment", "Order, Order ID, or User ID is null while accepting order");
        }
    }

    private void moveOrderToCompleted(orders order) {
        DatabaseReference completedOrdersRef = FirebaseDatabase.getInstance().getReference("completed_orders");

        orders completedOrder = new orders();
        completedOrder.setOrderId(order.getOrderId());
        completedOrder.setOrderDate(order.getOrderDate());
        completedOrder.setOrderStatus("To ship");
        completedOrder.setProductName(order.getProductName());
        completedOrder.setUserId(order.getUserId());
        completedOrder.setCustomerName(order.getCustomerName());
        completedOrder.setOrderTotal(order.getOrderTotal());
        completedOrder.setPaymentType(order.getPaymentType());

        completedOrdersRef.child(order.getUserId()).child(order.getOrderId()).setValue(completedOrder)
                .addOnSuccessListener(aVoid -> Log.d("PendingOrderFragment", "Order successfully added to completed orders."))
                .addOnFailureListener(e -> Log.e("PendingOrderFragment", "Failed to add order to completed orders: " + e.getMessage()));
    }


    private void listenForOrderStatusUpdates(String userId, String orderId) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders")
                .child(userId).child(orderId);

        orderRef.child("orderStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if ("To ship".equals(status)) {
                        updateOrderStatusInUI(orderId, status);
                    }
                } else {
                    Log.w("PendingOrderFragment", "Order status snapshot does not exist for orderId: " + orderId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PendingOrderFragment", "Error updating order status", error.toException());
            }
        });
    }


    private void updateOrderStatusInUI(String orderId, String status) {
        if (adapter != null) {
            adapter.updateOrderStatus(orderId, status);
        }
    }

}
