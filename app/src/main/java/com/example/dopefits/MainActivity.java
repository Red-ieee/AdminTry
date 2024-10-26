package com.example.dopefits;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.dopefits.Fragments.ARFragment;
import com.example.dopefits.Fragments.AboutFragment;
import com.example.dopefits.Fragments.DataPolicyFragment;
import com.example.dopefits.Fragments.HomepageFragment;
import com.example.dopefits.Fragments.InventoryFragment;
import com.example.dopefits.Fragments.ProfileFragment;
import com.example.dopefits.Fragments.SalesFragment;
import com.example.dopefits.Fragments.TermsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView botNav;
    private DrawerLayout drawerLayout;
    private TextView menuAR, menuDataPolicy, menuTerms, menuAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize DrawerLayout and menu items
        drawerLayout = findViewById(R.id.drawer_layout);
        menuAR = findViewById(R.id.menuAR);
        menuDataPolicy = findViewById(R.id.menuDataPolicy);
        menuTerms = findViewById(R.id.menuTerms);
        menuAbout = findViewById(R.id.menuAbout);

        // Bottom Navigation setup
        botNav = findViewById(R.id.bottomNavigationView);
        loadFragment(new HomepageFragment()); // Load the default fragment

        botNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.home) {
                    selectedFragment = new HomepageFragment();
                } else if (item.getItemId() == R.id.sales) {
                    selectedFragment = new SalesFragment();
                } else if (item.getItemId() == R.id.inventory) {
                    selectedFragment = new InventoryFragment();
                } else if (item.getItemId() == R.id.profile) {
                    selectedFragment = new ProfileFragment();
                }
                return loadFragment(selectedFragment);
            }
        });


        // Handle menu icon click to open/close the drawer
        findViewById(R.id.sideNavMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) { // Use LEFT for compatibility
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });

        // Set click listeners for side navigation menu items
        menuAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load AR fragment or activity
                loadFragment(new ARFragment());
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        menuDataPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load Data Policy fragment or activity
                loadFragment(new DataPolicyFragment());
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        menuTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load Terms of Service fragment or activity
                loadFragment(new TermsFragment());
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        menuAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load About Dopefits fragment or activity
                loadFragment(new AboutFragment());
                drawerLayout.closeDrawer(Gravity.LEFT);
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

    @Override
    public void onBackPressed() {
        // Close the drawer if it's open
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }
}
