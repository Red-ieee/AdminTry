package com.example.dopefits.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dopefits.AdapterClass.CompletedOrderAdapter;
import com.example.dopefits.DataModel.orders;
import com.example.dopefits.OrderDetailActivity;
import com.example.dopefits.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class CompletedOrderFragment extends Fragment implements CompletedOrderAdapter.OnItemClickListener {
    private RecyclerView recyclerViewCompletedOrders;
    private CompletedOrderAdapter adapter;
    private DatabaseReference completedOrdersRef;
    private List<orders> completedOrderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_order, container, false);

        recyclerViewCompletedOrders = view.findViewById(R.id.recyclerViewCompletedOrder);
        recyclerViewCompletedOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        completedOrderList = new ArrayList<>();
        adapter = new CompletedOrderAdapter(getContext(), completedOrderList, this);
        recyclerViewCompletedOrders.setAdapter(adapter);

        completedOrdersRef = FirebaseDatabase.getInstance().getReference("completed_orders");

        fetchCompletedOrders();

        return view;
    }

    private void fetchCompletedOrders() {
        completedOrdersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                completedOrderList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {

                        String customerName = orderSnapshot.child("customerName").getValue(String.class);
                        String orderDate = orderSnapshot.child("orderDate").getValue(String.class);
                        String orderTotal = orderSnapshot.child("orderTotal").getValue(String.class);
                        String paymentType = orderSnapshot.child("paymentType").getValue(String.class);
                        String orderStatus = orderSnapshot.child("orderStatus").getValue(String.class);
                        String productName = orderSnapshot.child("productName").getValue(String.class);
                        String orderId = orderSnapshot.getKey();

                        if (customerName != null && orderDate != null) {
                            orders order = new orders();
                            order.setUserId(userId);
                            order.setCustomerName(customerName);
                            order.setOrderDate(orderDate);
                            order.setOrderTotal(orderTotal);
                            order.setPaymentType(paymentType);
                            order.setOrderStatus(orderStatus);
                            order.setProductName(productName);
                            order.setOrderId(orderId);

                            Log.d("Firebase", "Price: " + order.getOrderTotal() );
                            Log.d("Firebase", "Payment Type: " + order.getPaymentType());

                            completedOrderList.add(order);
                        } else {
                            Log.e("CompletedOrderFragment", "Missing fields for order: " + orderSnapshot.getKey());
                        }
                    }
                }

                Collections.sort(completedOrderList, new Comparator<orders>() {
                    @Override
                    public int compare(orders o1, orders o2) {
                        long date1 = parseDateStringToLong(o1.getOrderDate());
                        long date2 = parseDateStringToLong(o2.getOrderDate());
                        return Long.compare(date2, date1);
                    }
                });

                Log.d("CompletedOrderFragment", "Number of completed orders: " + completedOrderList.size());

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("CompletedOrderFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }


    private long parseDateStringToLong(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = dateFormat.parse(dateString);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            Log.e("CompletedOrderFragment", "Error parsing date: " + dateString, e);
            return 0;
        }
    }

    @Override
    public void onItemClick(orders order) {
        Context context = requireContext();
        Intent intent = new Intent(context, OrderDetailActivity.class);

        Log.d("CompletedOrderFragment", "Passing Price: " + order.getOrderTotal());
        Log.d("CompletedOrderFragment", "Passing Payment Type: " + order.getPaymentType());

        intent.putExtra("customerName", order.getCustomerName());
        intent.putExtra("orderId", order.getOrderId());
        intent.putExtra("orderItem", order.getProductName());
        intent.putExtra("orderDate", order.getOrderDate());
        intent.putExtra("orderTotal", order.getOrderTotal());
        intent.putExtra("paymentType", order.getPaymentType());
        intent.putExtra("status", order.getOrderStatus());
        intent.putExtra("userId", order.getUserId());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }
}