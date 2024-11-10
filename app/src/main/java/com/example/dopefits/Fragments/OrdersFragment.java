package com.example.dopefits.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.dopefits.R;

public class OrdersFragment extends Fragment {
    private TextView tabCompletedOrders, tabPendingOrders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        tabCompletedOrders = view.findViewById(R.id.tabCompletedOrders);
        tabPendingOrders = view.findViewById(R.id.tabPendingOrders);

        loadFragment(new CompletedOrderFragment());
        tabCompletedOrders.setSelected(true);

        tabCompletedOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OrdersFragment", "Completed Orders tab clicked");
                loadFragment(new CompletedOrderFragment()); // Load Completed Orders
            }
        });

        tabPendingOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OrdersFragment", "Pending Orders tab clicked");
                loadFragment(new PendingOrderFragment());
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        if (getActivity() != null) {
            Log.d("OrdersFragment", "Loading fragment: " + fragment.getClass().getSimpleName());
            FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager()
            fragmentManager.beginTransaction()
                    .replace(R.id.FragmentContainer, fragment) // Make sure this ID is correct in your layout
                    .commit();
        }
    }
}
