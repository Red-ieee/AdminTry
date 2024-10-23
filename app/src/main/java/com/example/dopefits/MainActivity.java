package com.example.dopefits;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.dopefits.Fragments.FragmentHomepage;
import com.example.dopefits.Fragments.FragmentInventory;
import com.example.dopefits.Fragments.FragmentProfile;
import com.example.dopefits.Fragments.FragmentSales;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView botNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botNav = findViewById(R.id.bottomNavigationView);
        loadFragment(new FragmentHomepage()); // Load the default fragment

        botNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.home) {
                    selectedFragment = new FragmentHomepage();
                } else if (item.getItemId() == R.id.sales) {
                    selectedFragment = new FragmentSales();
                } else if (item.getItemId() == R.id.inventory) {
                    selectedFragment = new FragmentInventory();
                } else if (item.getItemId() == R.id.profile) {
                    selectedFragment = new FragmentProfile();
                }
                return loadFragment(selectedFragment);
            }
        });
    }

    // Method to load the selected fragment into the FrameLayout
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
