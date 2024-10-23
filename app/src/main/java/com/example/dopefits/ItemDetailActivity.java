package com.example.dopefits;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dopefits.AdapterClass.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ItemDetailActivity extends AppCompatActivity {
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

    private void showEditItemDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_item, null);

        // Find the EditText fields in the dialog layout
        EditText editTitle = dialogView.findViewById(R.id.editTitle);
        EditText editPrice = dialogView.findViewById(R.id.editPrice);
        EditText editQuantity = dialogView.findViewById(R.id.editQuantity);
        EditText editSize = dialogView.findViewById(R.id.editSize);
        EditText editBrand = dialogView.findViewById(R.id.editBrand);
        EditText editCondition = dialogView.findViewById(R.id.editCondition);
        EditText editIssue = dialogView.findViewById(R.id.editIssue);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);

        // Set existing item details to the EditText fields
        editTitle.setText(titleTextView.getText().toString());
        editPrice.setText(priceTextView.getText().toString().replace("₱", ""));
        editQuantity.setText(qtyTextView.getText().toString().replace("x", ""));
        editSize.setText(sizeTextView.getText().toString());
        editBrand.setText(brandTextView.getText().toString().replace("Brand: ", ""));
        editCondition.setText(conditionTextView.getText().toString().replace("Condition: ", ""));
        editIssue.setText(issueTextView.getText().toString().replace("Issue: ", ""));
        editDescription.setText(descriptionTextView.getText().toString());

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Item");
        builder.setView(dialogView);
        builder.setPositiveButton("Save", null); // We'll override this later
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override the Save button click to validate inputs before saving
        Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        saveButton.setOnClickListener(v -> {
            // Perform validation, ensure inputs are correct
            String newTitle = editTitle.getText().toString().trim();
            String newPrice = editPrice.getText().toString().trim();
            String newQuantity = editQuantity.getText().toString().trim();
            String newSize = editSize.getText().toString().trim();
            String newBrand = editBrand.getText().toString().trim();
            String newCondition = editCondition.getText().toString().trim();
            String newIssue = editIssue.getText().toString().trim();
            String newDescription = editDescription.getText().toString().trim();

            if (newTitle.isEmpty() || newPrice.isEmpty() || newQuantity.isEmpty()) {
                Toast.makeText(ItemDetailActivity.this, "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            } else {
                // Update the item's information
                titleTextView.setText(newTitle);
                priceTextView.setText("₱" + newPrice);
                qtyTextView.setText(newQuantity + "x");
                sizeTextView.setText(newSize);
                brandTextView.setText("Brand: " + newBrand);
                conditionTextView.setText("Condition: " + newCondition);
                issueTextView.setText("Issue: " + newIssue);
                descriptionTextView.setText(newDescription);

                // Update in Firebase (if required)
                updateItemInFirebase(newTitle, newPrice, newQuantity, newSize, newBrand, newCondition, newIssue, newDescription);

                dialog.dismiss(); // Close the dialog after saving
            }
        });
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



