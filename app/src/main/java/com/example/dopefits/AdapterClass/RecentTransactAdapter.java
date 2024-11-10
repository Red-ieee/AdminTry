package com.example.dopefits.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dopefits.DataModel.RecentTransact;
import com.example.dopefits.DataModel.orders;
import com.example.dopefits.R;

import java.util.List;

public class RecentTransactAdapter extends RecyclerView.Adapter<RecentTransactAdapter.ViewHolder> {
    private List<RecentTransact> transactionList;

    public RecentTransactAdapter(List<RecentTransact> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_transact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentTransact transaction = transactionList.get(position);
        holder.tvCustomerName.setText(transaction.getCustomerName());
        holder.tvItemName.setText(transaction.getItemName());
        holder.tvItemPrice.setText(transaction.getPrice());
        holder.tvPayMethod.setText(transaction.getPaymentType());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvItemName, tvItemPrice, tvPayMethod;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvPayMethod = itemView.findViewById(R.id.tvPayMethod);
        }
    }
}

