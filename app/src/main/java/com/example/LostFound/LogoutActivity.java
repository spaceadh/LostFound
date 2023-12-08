package com.example.LostFound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {

    private Button yesButton;
    private Button noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the activity to always use night mode
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout action
                logout();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SettingIconActivity
                navigateToSettingIconActivity();
            }
        });
    }

    private void logout() {
        // Perform logout action
        FirebaseAuth.getInstance().signOut();

        // Show toast message indicating successful logout
        //Toast.makeText(LogoutActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        showToast("Logged out successfully",Toast.LENGTH_SHORT);
        // Navigate to HomepageActivity
        Intent intent = new Intent(LogoutActivity.this, LoginNormal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToSettingIconActivity() {
        // Navigate to SettingIconActivity
        Intent intent = new Intent(LogoutActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
    void showToast(String koffeeLogoutActivity, int duration) {
        Toast toast = Toast.makeText(this, koffeeLogoutActivity, duration);

        View view = LayoutInflater.from(this)
                .inflate(R.layout.toast_layout, null);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        ImageView tvImage = view.findViewById(R.id.toast_icon);
        tvMessage.setText(koffeeLogoutActivity);

        Glide.with(this)
                .load(R.drawable.output2)
                .into(tvImage);

        toast.setView(view);
        toast.show();
    }
}