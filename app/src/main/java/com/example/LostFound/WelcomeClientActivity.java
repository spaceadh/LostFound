package com.example.LostFound;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.LostFound.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeClientActivity extends AppCompatActivity {

    private static final long DELAY_TIME = 2000; // Delay time in milliseconds
    private TextView textViewGreeting;
    private TextView textViewUsername;
    private String currentUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Set the activity to always use night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_client);

        textViewGreeting = findViewById(R.id.textView_greeting);
        textViewUsername = findViewById(R.id.textView_username);

        String greeting = "Hello";
        // Get the current time
        textViewGreeting.setText(greeting);

        // Retrieve the current username
        getCurrentUsername();

        // Delay and move to the next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the next activity
                Intent intent = new Intent(WelcomeClientActivity.this, DashboardActivity.class);
                startActivity(intent);

                // Finish this activity
                finish();
            }
        }, DELAY_TIME);
    }
    private void getCurrentUsername() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child("Clients").child(currentUserId);
            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            currentUsername = user.getUsername();
                            textViewUsername.setText(currentUsername);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }
    }
}