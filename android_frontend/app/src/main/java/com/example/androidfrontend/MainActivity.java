package com.example.androidfrontend;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main activity for QuickPoll App.
 * Hosts bottom navigation for Polls and Create screens.
 */
public class MainActivity extends AppCompatActivity {

    // PUBLIC_INTERFACE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Initializes the main activity and sets up bottom navigation.
         */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PollListFragment())
                .commit();
        }
    }

    // PUBLIC_INTERFACE
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /**
         * Handles navigation item selection.
         * Replaces switch statement with if-else to avoid constant expression error.
         */
        Fragment selectedFragment;
        int id = item.getItemId();
        if (id == R.id.nav_polls) {
            selectedFragment = new PollListFragment();
        } else if (id == R.id.nav_create) {
            selectedFragment = new CreatePollFragment();
        } else {
            selectedFragment = new PollListFragment();
        }
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, selectedFragment)
            .commit();
        return true;
    }
}
