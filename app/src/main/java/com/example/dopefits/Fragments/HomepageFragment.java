package com.example.dopefits.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.dopefits.R;
import com.example.dopefits.AdapterClass.RecentTransactAdapter;
import com.example.dopefits.DataModel.RecentTransact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class    HomepageFragment extends Fragment {

    private RecyclerView RecentTransactRecyclerView;
    private RecentTransactAdapter recentTransactAdapter;
    private DatabaseReference transactionsRef;
    private List<RecentTransact> recentTransactionsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        RecentTransactRecyclerView = view.findViewById(R.id.RecentTransactRecyclerView);
        RecentTransactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        RecyclerView recyclerView = view.findViewById(R.id.RecentTransactRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        transactionsRef = FirebaseDatabase.getInstance().getReference("purchases");
        fetchRecentTransactions();

        return view;
    }

    private void fetchRecentTransactions() {
        transactionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recentTransactionsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RecentTransact transaction = snapshot.getValue(RecentTransact.class);
                    if (transaction != null) {
                        recentTransactionsList.add(transaction);
                    }
                }
                recentTransactAdapter = new RecentTransactAdapter(recentTransactionsList);
                RecentTransactRecyclerView.setAdapter(recentTransactAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("HomepageFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }
}

