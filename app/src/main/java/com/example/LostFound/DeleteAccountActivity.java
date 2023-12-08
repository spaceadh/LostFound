package com.example.LostFound;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccountActivity extends AppCompatActivity {

    private EditText deleteConfirmationEditText;
    private Button yesButton;
    private LinearLayout layout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the activity to always use night mode
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        // Making notification bar transparent
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
        setContentView(R.layout.activity_delete_account);

        deleteConfirmationEditText = findViewById(R.id.delete_confirmation);
        yesButton = findViewById(R.id.yesButton);
        layout = findViewById(R.id.buttonContainer);

        firebaseAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference("users");

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle layout click event here
                // For example, you can show a toast message
                Toast.makeText(DeleteAccountActivity.this, "Layout clicked", Toast.LENGTH_SHORT).show();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmationText = deleteConfirmationEditText.getText().toString().trim();
                if (confirmationText.equalsIgnoreCase("DELETE")) {
                    deleteAccount();

                    // Remove focus from the EditText field
                    deleteConfirmationEditText.clearFocus();
                } else {
                    showToast("Incorrect confirmation text",Toast.LENGTH_SHORT);
                    //Toast.makeText(DeleteAccountActivity.this, "Incorrect confirmation text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteAccount() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userReference.child(userId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth.getCurrentUser().delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // User account deleted successfully
                                                    showToast("Account Deleted successfully",Toast.LENGTH_SHORT);
                                                    // Navigate to HomepageActivity
                                                    Intent intent = new Intent(DeleteAccountActivity.this,SignupActivity.class);
                                                    startActivity(intent);
                                                    finish(); // Optional: Close the current activity if desired
                                                } else {
                                                    // Failed to delete user account
                                                    // Show error message or dialog
                                                    showErrorMessage("Failed to delete user account. Please try again.");
                                                }
                                            }
                                        });
                            } else {
                                // Failed to delete account from the database
                                // Show error message or dialog
                                showErrorMessage("Failed to delete account from the database. Please try again.");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to delete account from the database
                            // Show error message or dialog
                            showErrorMessage("Failed to delete account from the database. Please try again.");
                        }
                    });
        }
    }

    private void showErrorMessage(String message) {
        // Show error message to the user (e.g., using Toast or a dialog)
        Toast.makeText(DeleteAccountActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    void showToast(String koffeeDeleteAccountActivity, int duration) {
        Toast toast = Toast.makeText(this, koffeeDeleteAccountActivity, duration);

        View view = LayoutInflater.from(this)
                .inflate(R.layout.toast_layout, null);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        ImageView tvImage = view.findViewById(R.id.toast_icon);
        tvMessage.setText(koffeeDeleteAccountActivity);

        Glide.with(this)
                .load(R.drawable.output2)
                .into(tvImage);

        toast.setView(view);
        toast.show();
    }
}