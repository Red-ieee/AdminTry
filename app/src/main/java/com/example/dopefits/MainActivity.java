package com.example.dopefits;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.dopefits.Fragments.HomepageFragment;
import com.example.dopefits.Fragments.InventoryFragment;
import com.example.dopefits.Fragments.ProfileFragment;
import com.example.dopefits.Fragments.OrdersFragment;
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
                    selectedFragment = new OrdersFragment();
                } else if (item.getItemId() == R.id.inventory) {
                    selectedFragment = new InventoryFragment();
                } else if (item.getItemId() == R.id.profile) {
                    selectedFragment = new ProfileFragment();
                }
                return loadFragment(selectedFragment);
            }
        });
    }


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
