package com.example.dopefits.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.dopefits.AdapterClass.ItemsAdapter;
import com.example.dopefits.DataModel.Items;
import com.example.dopefits.R;
import com.example.dopefits.UploadActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class FragmentInventory extends Fragment {
    private static final int DELETE_REQUEST_CODE = 1;

    private RecyclerView categoryRecyclerView, clothesRecyclerView;
    private ItemsAdapter itemsAdapter;
    private List<Items> itemsList;
    private FirebaseDatabase database;
    private DatabaseReference itemsRef;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase setup
        database = FirebaseDatabase.getInstance();
        itemsRef = database.getReference("Items");

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        clothesRecyclerView = view.findViewById(R.id.clothesRecyclerView);
        clothesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        itemsList = new ArrayList<>();

        // Create ItemsAdapter and set it to the RecyclerView
        itemsAdapter = new ItemsAdapter(getContext(), itemsList);
        clothesRecyclerView.setAdapter(itemsAdapter);

        // Floating Action Button setup
        fab = view.findViewById(R.id.AddButton); // Access the FAB within the fragment's layout
        fab.setOnClickListener(v -> onFabClicked());

        fetchAllItems();
    }

    private void fetchAllItems() {
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemsList.clear(); // Clear the previous list
                int totalItemsFetched = 0; // Track total items fetched

                // Log the total number of items in Firebase
                Log.d("FragmentInventory", "Total items in Firebase: " + snapshot.getChildrenCount());

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Log the entire snapshot for debugging
                    Log.d("FragmentInventory", "DataSnapshot: " + dataSnapshot.toString());

                    // Retrieve fields from Firebase
                    Integer categoryId = dataSnapshot.child("categoryId").getValue(Integer.class);
                    List<String> picUrlList = (List<String>) dataSnapshot.child("picUrl").getValue();
                    String title = dataSnapshot.child("title").getValue(String.class);
                    Integer priceValue = dataSnapshot.child("price").getValue(Integer.class);
                    Integer quantityValue = dataSnapshot.child("quantity").getValue(Integer.class);
                    String size = dataSnapshot.child("size").getValue(String.class);
                    String brand = dataSnapshot.child("brand").getValue(String.class);
                    String condition = dataSnapshot.child("condition").getValue(String.class);
                    String issue = dataSnapshot.child("issue").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String firebaseKey = dataSnapshot.getKey();

                    // Log fetched values
                    Log.d("FragmentInventory", "Fetched item: title=" + title + ", price=" + priceValue + ", quantity=" + quantityValue);

                    // Validate the required fields
                    if (title != null && priceValue != null && quantityValue != null) {
                        int price = priceValue; // Unbox to int
                        int quantity = quantityValue; // Unbox to int

                        // Create item and add to the list
                        Items items = new Items(categoryId, picUrlList, title, priceValue, quantityValue, size, brand, condition, issue, description, firebaseKey);
                        itemsList.add(items);
                        totalItemsFetched++; // Increment for each item added
                    } else {
                        Log.e("FragmentInventory", "Item is missing title, price, or quantity: " + dataSnapshot.toString());
                    }
                }

                Log.d("FragmentInventory", "Total items fetched: " + totalItemsFetched); // Log the total
                itemsAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onFabClicked() {
        Intent intent = new Intent(getActivity(), UploadActivity.class);
        startActivityForResult(intent, 100);  // Request code 100 for the upload activity
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ItemsAdapter.DELETE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            if (position != -1) {
                itemsAdapter.removeItem(position); // Assuming you have a reference to the adapter
            }
        }
    }
}


