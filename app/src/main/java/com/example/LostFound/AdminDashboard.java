package com.example.LostFound;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.LostFound.AdminFragment.AdminHomeFragment;
import com.example.LostFound.AdminFragment.HomepageSwitcheroo;
import com.example.LostFound.ClientsFragments.HomeFragment;
import com.example.LostFound.ClientsFragments.ProfileFragment;
import com.example.LostFound.ClientsFragments.InstructionsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminDashboard extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private HomepageSwitcheroo adminHomeFragment;
    private ProfileFragment settingsFragment;
    private InstructionsFragment instructionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content, adminHomeFragment)
                                .commit();
                        return true;
                    case R.id.nav_settings:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content, settingsFragment)
                                .commit();
                        return true;
                    case R.id.nav_transaction_cost:
                        startActivity(new Intent(AdminDashboard.this, AddingLostProductActivity.class));
                        return false;
                }
                return false;
            }
        });


        adminHomeFragment = new HomepageSwitcheroo();
        settingsFragment = new ProfileFragment();
        instructionsFragment = new InstructionsFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, adminHomeFragment)
                .commit();
    }

}