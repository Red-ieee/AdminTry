package com.example.dopefits.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dopefits.DataModel.RecentTransact;
import com.example.dopefits.R;

import java.util.List;

public class RecentTransactAdapter extends RecyclerView.Adapter<RecentTransactAdapter.ViewHolder> {
    private Context context;
    private List<RecentTransact> recentTransacts;

    public RecentTransactAdapter (Context context, List<RecentTransact> recentTransacts) {
        this.context = context;
        this.recentTransacts = recentTransacts;
    }

    @NonNull
    @Override
    public RecentTransactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_transact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentTransactAdapter.ViewHolder holder, int position) {
        RecentTransact recentTransact = recentTransacts.get(position);
        holder.tvCustomerName.setText(recentTransact.getCustomerName());
        holder.tvItemName.setText(recentTransact.getItemName());
        holder.tvItemPrice.setText(String.valueOf(recentTransact.getPrice()));
        holder.tvQuantity.setText(String.valueOf(recentTransact.getQty())+"x");

    }

    @Override
    public int getItemCount() {
        return recentTransacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvItemName, tvItemPrice, tvQuantity;

        public ViewHolder(View view) {
            super(view);
            tvCustomerName = view.findViewById(R.id.tvCustomerName);
            tvItemName = view.findViewById(R.id.tvItemName);
            tvItemPrice = view.findViewById(R.id.tvItemPrice);
            tvQuantity = view.findViewById(R.id.tvQuantity);
        }
    }
}

