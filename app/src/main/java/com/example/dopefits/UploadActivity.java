    package com.example.dopefits;

    import android.Manifest;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Environment;
    import android.provider.Settings;
    import android.text.TextUtils;
    import android.util.Log;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;

    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class UploadActivity extends AppCompatActivity {
        private static final int PICK_IMAGE_REQUEST = 1;
        private static final int PERMISSION_REQUEST_CODE = 2;

        private ImageView ItemsImageUpload;
        private EditText UploadTitle, UploadBrand, UploadCondition, UploadDescription, UploadSize, UploadIssue, UploadPrice, UploadQuantity;
        private TextView imageSelected;
        private Spinner spinnerCategory;
        private Button uploadButton;
        private List<Uri> imageUriList = new ArrayList<>(); // List to store multiple image URIs
        private List<String> uploadedImageUrls = new ArrayList<>(); // List to store uploaded image URLs

        private StorageReference storageReference;
        private DatabaseReference databaseReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_upload);

            // Initialize Firebase
            storageReference = FirebaseStorage.getInstance().getReference("items");
            databaseReference = FirebaseDatabase.getInstance().getReference();

            // Initialize UI elements
            ItemsImageUpload = findViewById(R.id.ItemsImageUpload);
            UploadTitle = findViewById(R.id.UploadTitle);
            UploadBrand = findViewById(R.id.UploadBrand);
            UploadCondition = findViewById(R.id.UploadCondition);
            UploadDescription = findViewById(R.id.UploadDescription);
            UploadSize = findViewById(R.id.UploadSize);
            UploadIssue = findViewById(R.id.UploadIssue);
            UploadPrice = findViewById(R.id.UploadPrice);
            UploadQuantity = findViewById(R.id.UploadQuantity);
            spinnerCategory = findViewById(R.id.spinnerCategory);
            uploadButton = findViewById(R.id.UploadButton);

            // TextView for showing if images are selected
            imageSelected = findViewById(R.id.ImageSelected);
            imageSelected.setVisibility(View.INVISIBLE);

            // Load categories from Firebase and populate the spinner
            fetchCategories();

            // Set onClickListener for image selection
            ItemsImageUpload.setOnClickListener(v -> checkStoragePermission());

            // Set onClickListener for upload button
            uploadButton.setOnClickListener(v -> uploadItem());
        }

        // Method to check for storage permission
        private void checkStoragePermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // For Android 11 and above
                if (!Environment.isExternalStorageManager()) {
                    // Ask for MANAGE_EXTERNAL_STORAGE permission
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                } else {
                    openFileChooser();
                }
            } else {
                // For Android below 11
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                } else {
                    openFileChooser();
                }
            }
        }

        // Handle the result of the permission request
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, open file chooser
                    openFileChooser();
                } else {
                    // Permission denied, show a message
                    Toast.makeText(this, "Permission denied to access storage", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Open file chooser to select multiple images
        private void openFileChooser() {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple image selection
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    // Multiple images were selected
                    int count = data.getClipData().getItemCount();
                    imageUriList.clear();

                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUriList.add(imageUri); // Add to list
                        Log.d("Image URI", imageUri.toString()); // Log the image URI
                    }

                    imageSelected.setVisibility(View.VISIBLE); // Indicate that images are selected
                    ItemsImageUpload.setImageURI(imageUriList.get(0)); // Display the first selected image as a preview

                } else if (data.getData() != null) {
                    // Single image selected
                    Uri imageUri = data.getData();
                    imageUriList.clear();
                    imageUriList.add(imageUri);
                    Log.d("Image URI", imageUri.toString());

                    imageSelected.setVisibility(View.VISIBLE);
                    ItemsImageUpload.setImageURI(imageUri);
                }
            }
        }

        // Method to fetch categories from Firebase Realtime Database
        private void fetchCategories() {
            DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Category");

            categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> categories = new ArrayList<>();
                    List<Integer> categoryIds = new ArrayList<>(); // Store category IDs for mapping later

                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String categoryTitle = categorySnapshot.child("title").getValue(String.class);
                        Integer categoryId = categorySnapshot.child("id").getValue(Integer.class);

                        categories.add(categoryTitle);
                        categoryIds.add(categoryId); // Store category IDs
                    }

                    // Populate spinner with categories
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UploadActivity.this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);

                    // Set tag to store category IDs
                    spinnerCategory.setTag(categoryIds);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(UploadActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Method to upload the item to Firebase
        private void uploadItem() {
            String title = UploadTitle.getText().toString();
            String brand = UploadBrand.getText().toString();
            String condition = UploadCondition.getText().toString();
            String description = UploadDescription.getText().toString();
            String size = UploadSize.getText().toString();
            String issue = UploadIssue.getText().toString();
            String price = UploadPrice.getText().toString();
            String quantity = UploadQuantity.getText().toString();

            // Get selected category ID
            int categoryIndex = spinnerCategory.getSelectedItemPosition();
            List<Integer> categoryIds = (List<Integer>) spinnerCategory.getTag();
            Integer selectedCategoryId = categoryIds.get(categoryIndex);

            // Validate required fields
            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(brand) || TextUtils.isEmpty(condition) ||
                    TextUtils.isEmpty(description) || TextUtils.isEmpty(size) || TextUtils.isEmpty(price) || imageUriList.isEmpty()) {
                Toast.makeText(this, "All fields and images are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch the highest current ID
            DatabaseReference itemsRef = databaseReference.child("Items");
            itemsRef.orderByChild("id").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int newId = 1; // Default to 1 if there are no items

                    // If there are items, get the highest ID and increment it
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            Integer currentId = itemSnapshot.child("id").getValue(Integer.class);
                            if (currentId != null) {
                                newId = currentId + 1;
                            }
                        }
                    }

                    // Proceed with the image upload after determining the new ID
                    uploadImagesWithId(newId, title, brand, condition, description, size, issue, price, quantity, selectedCategoryId);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UploadActivity.this, "Failed to fetch the current highest ID", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Method to handle the actual image upload process
        private void uploadImagesWithId(int id, String title, String brand, String condition, String description,
                                        String size, String issue, String price, String quantity, int selectedCategoryId) {
            uploadedImageUrls.clear();

            for (int i = 0; i < imageUriList.size(); i++) {
                Uri imageUri = imageUriList.get(i);
                StorageReference fileReference = storageReference.child(selectedCategoryId + "/" + System.currentTimeMillis() + "_" + i + ".jpg");

                // Upload each image
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            uploadedImageUrls.add(uri.toString()); // Add image URL to list

                            // If all images are uploaded, proceed to upload the item details
                            if (uploadedImageUrls.size() == imageUriList.size()) {
                                uploadItemWithUrls(id, title, brand, condition, description, size, issue, price, quantity, selectedCategoryId, uploadedImageUrls);
                            }
                        }))
                        .addOnFailureListener(e -> Toast.makeText(UploadActivity.this, "Image upload failed!", Toast.LENGTH_SHORT).show());
            }
        }

        // Method to handle the actual item upload with image URLs
        private void uploadItemWithUrls(int id, String title, String brand, String condition, String description,
                                        String size, String issue, String price, String quantity, int selectedCategoryId, List<String> imageUrls) {
            // Create item object
            Map<String, Object> item = new HashMap<>();
            item.put("id", id); // Add the new ID here
            item.put("title", title);
            item.put("brand", brand);
            item.put("condition", condition);
            item.put("description", description);
            item.put("size", size);
            item.put("issue", issue);
            item.put("price", Integer.parseInt(price));
            item.put("quantity", Integer.parseInt(quantity));
            item.put("categoryId", selectedCategoryId);
            item.put("picUrl", imageUrls); // Store the list of image URLs

            // Save to Firebase Realtime Database
            DatabaseReference itemsRef = databaseReference.child("Items");
            itemsRef.push().setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UploadActivity.this, "Item uploaded successfully!", Toast.LENGTH_SHORT).show();

                        // Create an Intent to pass the new item data back
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("newItem", (HashMap) item);  // Convert item to HashMap to pass via Intent

                        setResult(RESULT_OK, resultIntent); // Send result back to FragmentInventory
                        finish(); // Close the activity after successful upload
                    })
                    .addOnFailureListener(e -> Toast.makeText(UploadActivity.this, "Failed to upload item", Toast.LENGTH_SHORT).show());
        }

    }
