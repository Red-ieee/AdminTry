package com.example.dopefits.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.dopefits.R;

public class EditItemDialogFragment extends DialogFragment {

    private EditText editTitle, editPrice, editQuantity, editSize, editBrand, editCondition, editIssue, editDescription;
    private EditItemListener listener;

    // Callback interface to pass data back to the activity
    public interface EditItemListener {
        void onItemUpdated(String title, String price, String quantity, String size, String brand, String condition, String issue, String description);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditItemListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EditItemListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate your dialog layout
        View view = inflater.inflate(R.layout.dialog_edit_item, null);

        // Find your EditText fields
        editTitle = view.findViewById(R.id.editTitle);
        editPrice = view.findViewById(R.id.editPrice);
        editQuantity = view.findViewById(R.id.editQuantity);
        editSize = view.findViewById(R.id.editSize);
        editBrand = view.findViewById(R.id.editBrand);
        editCondition = view.findViewById(R.id.editCondition);
        editIssue = view.findViewById(R.id.editIssue);
        editDescription = view.findViewById(R.id.editDescription);

        // Get existing item details from the arguments if any
        if (getArguments() != null) {
            editTitle.setText(getArguments().getString("title"));
            editPrice.setText(getArguments().getString("price"));
            editQuantity.setText(getArguments().getString("quantity"));
            editSize.setText(getArguments().getString("size"));
            editBrand.setText(getArguments().getString("brand"));
            editCondition.setText(getArguments().getString("condition"));
            editIssue.setText(getArguments().getString("issue"));
            editDescription.setText(getArguments().getString("description"));
        }

        // Set up the dialog buttons
        builder.setView(view)
                .setTitle("Edit Item")
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        AlertDialog dialog = builder.create();

        // Set the listener to run after the dialog is shown
        dialog.setOnShowListener(dialogInterface -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
                // Validate inputs and pass the data back
                String title = editTitle.getText().toString().trim();
                String price = editPrice.getText().toString().trim();
                String quantity = editQuantity.getText().toString().trim();
                String size = editSize.getText().toString().trim();
                String brand = editBrand.getText().toString().trim();
                String condition = editCondition.getText().toString().trim();
                String issue = editIssue.getText().toString().trim();
                String description = editDescription.getText().toString().trim();

                if (!title.isEmpty() && !price.isEmpty() && !quantity.isEmpty()) {
                    listener.onItemUpdated(title, price, quantity, size, brand, condition, issue, description);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please fill out all required fields", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return dialog;
    }
}