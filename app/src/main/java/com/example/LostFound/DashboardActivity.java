package com.example.LostFound;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.LostFound.ClientsFragments.HomeFragment;
import com.example.LostFound.ClientsFragments.ProfileFragment;
import com.example.LostFound.ClientsFragments.InstructionsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private ProfileFragment settingsFragment;
    private InstructionsFragment instructionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content, homeFragment)
                                .commit();
                        return true;
                    case R.id.nav_settings:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content, settingsFragment)
                                .commit();
                        return true;
                    case R.id.nav_transaction_cost:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content, instructionsFragment)
                                .commit();
                        return true;
                }
                return false;
            }
        });


        homeFragment = new HomeFragment();
        settingsFragment = new ProfileFragment();
        instructionsFragment = new InstructionsFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, homeFragment)
                .commit();
    }

}