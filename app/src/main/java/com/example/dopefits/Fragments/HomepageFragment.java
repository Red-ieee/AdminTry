package com.example.dopefits.Fragments;

import android.os.Bundle;
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


import java.util.ArrayList;
import java.util.List;

public class HomepageFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecentTransactAdapter recentTransactAdapter;
    private List<RecentTransact> recentTransactsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        recyclerView = view.findViewById(R.id.RecentTransactRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recentTransactsList = new ArrayList<>();
        recentTransactsList.add(new RecentTransact("John Doe", "Item 1", 10, 1));
        recentTransactsList.add(new RecentTransact("Jane Smith", "Item 2", 15, 2));
        recentTransactsList.add(new RecentTransact("Bob Brown", "Item 3", 8, 1));
        recentTransactsList.add(new RecentTransact("Alice Johnson", "Item 4", 20, 3));

        recentTransactAdapter = new RecentTransactAdapter(getContext(), recentTransactsList);
        recyclerView.setAdapter(recentTransactAdapter);

        return view;
    }
}
