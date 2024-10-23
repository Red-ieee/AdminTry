package com.example.dopefits.AdapterClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.dopefits.ItemDetailActivity;
import com.example.dopefits.R;
import com.example.dopefits.DataModel.Items;

import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ClothesViewHolder> {

    private Context context;
    private static List<Items> itemsList;

    public static final int DELETE_REQUEST_CODE = 1;


    public ItemsAdapter(Context context, List<Items> itemList) {
        this.context = context;
        this.itemsList = itemList;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < itemsList.size()) {
            itemsList.remove(position);
            notifyItemRemoved(position);
        }
    }



    @NonNull
    @Override
    public ClothesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.clothes_item, parent, false);
        return new ClothesViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ClothesViewHolder holder, int position) {
        Items item = itemsList.get(position);
        holder.title.setText(item.getTitle());

        // Load image using Glide
        Glide.with(context)
                .load(item.getPicUrl().get(0))
                .into(holder.imageView);

        // Set click listener to handle item clicks
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putStringArrayListExtra("picUrls", new ArrayList<>(item.getPicUrl()));
            intent.putExtra("title", item.getTitle());
            intent.putExtra("price", item.getPrice()); // Make sure getPrice() exists
            intent.putExtra("quantity", item.getQuantity()); // Make sure getQuantity() exists
            intent.putExtra("size", item.getSize()); // Make sure getSize() exists
            intent.putExtra("brand", item.getBrand()); // Make sure getBrand() exists
            intent.putExtra("condition", item.getCondition()); // Make sure getCondition() exists
            intent.putExtra("issue", item.getIssue()); // Make sure getIssue() exists
            intent.putExtra("description", item.getDescription());
            intent.putExtra("position", position);
            intent.putExtra("firebase_key", item.getFirebaseKey());
            Log.d("ItemsAdapter", "Firebase Key for item: " + item.getFirebaseKey());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    // ViewHolder class
    public static class ClothesViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;

        public ClothesViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.clothesTitle);
            imageView = itemView.findViewById(R.id.clothesImageView);
        }
    }
}
