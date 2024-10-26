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

    import androidx.appcompat.widget.SearchView;

    import com.example.dopefits.AdapterClass.CategoryAdapter;
    import com.example.dopefits.AdapterClass.ItemsAdapter;
    import com.example.dopefits.DataModel.Category;
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

    public class InventoryFragment extends Fragment {
        private static final int DELETE_REQUEST_CODE = 1;

        private RecyclerView categoryRecyclerView, itemsRecyclerView;
        private ItemsAdapter itemsAdapter;
        private CategoryAdapter categoryAdapter;
        private List<Items> itemsList;
        private List<Category> categoryList;
        private List<Items> originalItemsList;
        private FirebaseDatabase database;
        private DatabaseReference itemsRef, categoriesRef;
        private FloatingActionButton fab;
        private SearchView searchView;



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
            categoriesRef = database.getReference("Category");

            // Setting up RecyclerViews
            categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
            categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            itemsRecyclerView = view.findViewById(R.id.clothesRecyclerView);
            itemsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

            itemsList = new ArrayList<>();
            categoryList = new ArrayList<>();
            originalItemsList = new ArrayList<>();

            // Initializing adapters
            itemsAdapter = new ItemsAdapter(getContext(), itemsList);
            itemsRecyclerView.setAdapter(itemsAdapter);

            categoryAdapter = new CategoryAdapter(getContext(), categoryList, this::onCategorySelected);
            categoryRecyclerView.setAdapter(categoryAdapter);

            searchView = view.findViewById(R.id.searchView);

            fab = view.findViewById(R.id.AddButton);
            fab.setOnClickListener(v -> onFabClicked());

            fetchCategories();
            fetchAllItems();
            setupSearchView();
        }

        public void onResume() {
            super.onResume();
            fetchCategories();
            fetchAllItems();
        }

        private void setupSearchView() {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Perform filtering based on query text
                    filterItems(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Update the filter as the text changes
                    filterItems(newText);
                    return false;
                }
            });
        }

        private void filterItems(String query) {
            if (query == null || query.trim().isEmpty()) {
                // If the search query is empty, display all items
                itemsAdapter.updateItems(originalItemsList); // Reset to the original list
            } else {
                // Filter based on query
                List<Items> filteredItems = new ArrayList<>();
                for (Items item : originalItemsList) { // Filter from originalItemsList
                    if (item.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        filteredItems.add(item);
                    }
                }
                itemsAdapter.updateItems(filteredItems);
            }
            itemsAdapter.notifyDataSetChanged(); // Ensure the adapter is notified of changes
        }

        private void onCategorySelected(int categoryId) {
            Log.d("FragmentInventory", "onCategorySelected called with categoryId: " + categoryId);

            List<Items> filteredItems = new ArrayList<>();
            if (categoryId == -1) { // Check if "All items" is selected
                filteredItems.addAll(originalItemsList); // Show all items
            } else {
                for (Items item : originalItemsList) { // Filter from the original list
                    Log.d("FragmentInventory", "Item: " + item.getTitle() + ", Category ID: " + item.getCategoryId());
                    if (item.getCategoryId() == categoryId) {
                        filteredItems.add(item);
                    }
                }
            }

            Log.d("FragmentInventory", "Filtered items count: " + filteredItems.size());
            itemsAdapter.updateItems(filteredItems);
        }

        private void fetchAllItems() {
            itemsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    originalItemsList.clear(); // Clear the original list
                    itemsList.clear(); // Clear the current displayed list
                    int totalItemsFetched = 0; // Track total items fetched
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        // Log the entire snapshot for debugging
                        Log.d("FragmentInventory", "DataSnapshot: " + dataSnapshot.toString());

                        // Retrieve fields from Firebase
                        int categoryId = dataSnapshot.child("categoryId").getValue(Integer.class);
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

                            // Create item and add to the lists
                            Items items = new Items(categoryId, picUrlList, title, price, quantity, size, brand, condition, issue, description, firebaseKey);
                            originalItemsList.add(items); // Add to original list
                            itemsList.add(items); // Add to displayed list
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

        // Function to fetch all categories from Firebase
        private void fetchCategories() {
            categoriesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("CategoryFragmentInventory", "Snapshot contents: " + snapshot.toString());

                    categoryList.clear(); // Clear previous categories

                    // Add "All items" category
                    categoryList.add(new Category(-1, "All items")); // Using -1 for the category ID of "All items"

                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                        // Fetch the "id" and "title" fields from the snapshot
                        Integer id = categorySnapshot.child("id").getValue(Integer.class);
                        String title = categorySnapshot.child("title").getValue(String.class);

                        Log.d("CategoryFragmentInventory", "Fetched category: id=" + id + ", title=" + title);

                        if (id != null && title != null) {
                            // Create a Category object and add it to the list
                            Category category = new Category(id, title);
                            categoryList.add(category);
                        } else {
                            Log.e("CategoryFragmentInventory", "Category is missing id or title: " + categorySnapshot.toString());
                        }
                    }

                    categoryAdapter.notifyDataSetChanged(); // Notify adapter of changes
                    Log.d("CategoryFragmentInventory", "Total categories fetched: " + categoryList.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to fetch categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        // FAB click handler to open the UploadActivity
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
                    itemsAdapter.removeItem(position); // Assuming you have a method in your adapter to remove the item
                    itemsAdapter.notifyDataSetChanged();
                }
            }

            if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
                // Handle upload success and refresh the inventory
                fetchAllItems();
                Toast.makeText(getContext(), "Item uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
