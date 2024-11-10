package com.example.dopefits.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dopefits.DataModel.orders;
import com.example.dopefits.OrderDetailActivity;
import com.example.dopefits.R;

import java.util.List;

public class CompletedOrderAdapter extends RecyclerView.Adapter<CompletedOrderAdapter.ViewHolder> {
    private List<orders> completedOrderList;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(orders order);
    }

    // Change the constructor to pass context here
    public CompletedOrderAdapter(Context context, List<orders> completedOrdersList, OnItemClickListener listener) {
        this.context = context;
        this.completedOrderList = completedOrdersList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        orders order = completedOrderList.get(position);
        if (order != null) {
            holder.orderId.setText(order.getOrderId() != null ? order.getOrderId() : "ID not available");
            holder.orderDate.setText(order.getOrderDate() != null ? order.getOrderDate() : "Date not available");
            holder.orderName.setText(order.getProductName() != null ? order.getProductName() : "Product name not available");
            holder.orderStatus.setText(order.getOrderStatus() != null ? order.getOrderStatus() : "Status not available");
            holder.customerName.setText(order.getCustomerName() != null ? order.getCustomerName() : "Customer Name not available");


            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(order);

                    // Use itemView.getContext() instead of context
                    Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);
                    intent.putExtra("customerName", order.getCustomerName());
                    intent.putExtra("orderId", order.getOrderId());
                    intent.putExtra("orderItem", order.getProductName());
                    intent.putExtra("orderDate", order.getOrderDate());
                    intent.putExtra("price", order.getOrderTotal());
                    intent.putExtra("paymentType", order.getPaymentType());
                    intent.putExtra("status", order.getOrderStatus());

                    v.getContext().startActivity(intent);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return completedOrderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderName, orderDate, orderStatus, orderId, customerName, priceTextView, paymentTypeTextView;
        Button deliverButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            orderName = itemView.findViewById(R.id.orderName);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            customerName = itemView.findViewById(R.id.customerName);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            paymentTypeTextView = itemView.findViewById(R.id.paymentTypeTextView);
            deliverButton = itemView.findViewById(R.id.deliverButton);
        }
    }
}

