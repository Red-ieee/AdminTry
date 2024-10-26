package com.example.dopefits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dopefits.AdapterClass.ViewPagerAdapter;
import com.example.dopefits.Fragments.EditItemDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ItemDetailActivity extends AppCompatActivity implements EditItemDialogFragment.EditItemListener {
    private ViewPager2 viewPager2;
    private ImageView back, delete;
    private TextView priceTextView, qtyTextView, sizeTextView, titleTextView, brandTextView, conditionTextView, issueTextView, descriptionTextView, update;
    private BottomNavigationView botNav;
    private DatabaseReference databaseReference;
    private LinearLayout dotIndicatorLayout;
    private String firebaseKey;

    private int itemPosition; // Unique ID for the item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        databaseReference = FirebaseDatabase.getInstance().getReference("Items");
        firebaseKey = getIntent().getStringExtra("firebase_key");

        viewPager2 = findViewById(R.id.viewPager2);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);
        priceTextView = findViewById(R.id.PriceTextView);
        qtyTextView = findViewById(R.id.QtyTextView);
        sizeTextView = findViewById(R.id.SizeTextView);
        titleTextView = findViewById(R.id.TitleTextView);
        brandTextView = findViewById(R.id.BrandTextView);
        conditionTextView = findViewById(R.id.ConditionTextView);
        issueTextView = findViewById(R.id.IssueTextView);
        descriptionTextView = findViewById(R.id.DescriptionTextView);
        update = findViewById(R.id.update);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String title = bundle.getString("title");
            List<String> picUrls = bundle.getStringArrayList("picUrls");
            int price = bundle.getInt("price", 0);
            int quantity = bundle.getInt("quantity", 0);
            String size = bundle.getString("size");
            String brand = bundle.getString("brand");
            String condition = bundle.getString("condition");
            String issue = bundle.getString("issue");
            String description = bundle.getString("description");
            itemPosition = bundle.getInt("position", -1);

            titleTextView.setText(title);
            priceTextView.setText("₱" + price);
            qtyTextView.setText(quantity + "x");
            sizeTextView.setText(size);
            brandTextView.setText("Brand: " + brand);
            conditionTextView.setText("Condition: " + condition);
            issueTextView.setText("Issue: " + issue);
            descriptionTextView.setText(description);

            if (picUrls != null && !picUrls.isEmpty()) {
                ViewPagerAdapter adapter = new ViewPagerAdapter(this, picUrls);
                viewPager2.setAdapter(adapter);

                dotIndicatorLayout = findViewById(R.id.dotIndicatorLayout);
                setupDots(picUrls.size());

                // Add a page change listener to update dots when swiping
                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        updateDots(position);
                    }
                });
            }
        }

        // Back button to finish activity
        back.setOnClickListener(v -> {
            finish();
        });

        // Set up delete button click listener
        delete.setOnClickListener(v -> confirmDelete(firebaseKey));

        // Update button click listener to show the EditItemDialogFragment
        update.setOnClickListener(v -> showEditItemDialog());
    }

    // Method to set up dot indicators
    private void setupDots(int count) {
        dotIndicatorLayout.removeAllViews();
        Log.d("ItemDetailActivity", "Setting up dots: " + count);

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.bg_dot_selector);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);
            dotIndicatorLayout.addView(dot);
        }
        updateDots(0); // Set the first dot as active
    }

    // Method to update the state of the dot indicators
    private void updateDots(int position) {
        for (int i = 0; i < dotIndicatorLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) dotIndicatorLayout.getChildAt(i);
            if (i == position) {
                dot.setImageResource(R.drawable.bg_dot_active); // Active dot drawable
            } else {
                dot.setImageResource(R.drawable.bg_dot_inactive); // Inactive dot drawable
            }
        }
    }

    // Confirmation dialog for deletion
    private void confirmDelete(String firebaseKey) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteItem(firebaseKey); // Call deleteItem with the firebaseKey
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    // Method to handle the deletion
    private void deleteItem(String firebaseKey) {
        if (firebaseKey != null) {
            databaseReference.child(firebaseKey)
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ItemDetailActivity", "Item deleted successfully");
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("position", itemPosition);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish(); // Close the activity
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ItemDetailActivity", "Error deleting item: " + e.getMessage());
                        Toast.makeText(ItemDetailActivity.this, "Error deleting item", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("ItemDetailActivity", "Invalid firebaseKey: Item cannot be deleted.");
        }

    }

    // Method to show the EditItemDialogFragment
    private void showEditItemDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create a bundle to pass current item data to the dialog
        Bundle bundle = new Bundle();
        bundle.putString("title", titleTextView.getText().toString());
        bundle.putString("price", priceTextView.getText().toString().replace("₱", ""));
        bundle.putString("quantity", qtyTextView.getText().toString().replace("x", ""));
        bundle.putString("size", sizeTextView.getText().toString());
        bundle.putString("brand", brandTextView.getText().toString().replace("Brand: ", ""));
        bundle.putString("condition", conditionTextView.getText().toString().replace("Condition: ", ""));
        bundle.putString("issue", issueTextView.getText().toString().replace("Issue: ", ""));
        bundle.putString("description", descriptionTextView.getText().toString());

        // Show the dialog fragment
        EditItemDialogFragment editItemDialogFragment = new EditItemDialogFragment();
        editItemDialogFragment.setArguments(bundle);
        editItemDialogFragment.show(fragmentManager, "editItemDialog");
    }

    // Callback when the item is updated in the dialog
    @Override
    public void onItemUpdated(String title, String price, String quantity, String size, String brand, String condition, String issue, String description) {
        // Update the item's information
        titleTextView.setText(title);
        priceTextView.setText("₱" + price);
        qtyTextView.setText(quantity + "x");
        sizeTextView.setText(size);
        brandTextView.setText("Brand: " + brand);
        conditionTextView.setText("Condition: " + condition);
        issueTextView.setText("Issue: " + issue);
        descriptionTextView.setText(description);

        // Update in Firebase
        updateItemInFirebase(title, price, quantity, size, brand, condition, issue, description);
    }

    private void updateItemInFirebase(String title, String price, String quantity, String size, String brand, String condition, String issue, String description) {
        if (firebaseKey != null) {
            databaseReference.child(firebaseKey)
                    .child("title").setValue(title);
            databaseReference.child(firebaseKey)
                    .child("price").setValue(Integer.parseInt(price));
            databaseReference.child(firebaseKey)
                    .child("quantity").setValue(Integer.parseInt(quantity));
            databaseReference.child(firebaseKey)
                    .child("size").setValue(size);
            databaseReference.child(firebaseKey)
                    .child("brand").setValue(brand);
            databaseReference.child(firebaseKey)
                    .child("condition").setValue(condition);
            databaseReference.child(firebaseKey)
                    .child("issue").setValue(issue);
            databaseReference.child(firebaseKey)
                    .child("description").setValue(description);

            Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
