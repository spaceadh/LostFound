package com.example.LostFound;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.LostFound.Models.User;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity"; // Tag for logging
    private EditText editTextUsername, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonSignup;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the activity to always use night mode
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing Up...");
        mProgressDialog.setCancelable(false);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

//        firebaseDatabase.setPersistenceEnabled(true);
        firebaseDatabase = FirebaseDatabase.
                getInstance("https://lost-and-found-project-99acb-default-rtdb.firebaseio.com/");
        firebaseDatabase.getReference().keepSynced(true);

        // Set the buyer reference
        userReference = firebaseDatabase.getReference("Users")
                .child("Clients");

        editTextUsername = findViewById(R.id.username_input);
        editTextEmail = findViewById(R.id.emailinput);
        editTextPassword = findViewById(R.id.password_input);
        editTextConfirmPassword = findViewById(R.id.password_inputconfirm);
        buttonSignup = findViewById(R.id.sign_up_button);
        progressBar = findViewById(R.id.progress_bar);

        buttonSignup.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            signup(username, email, password, confirmPassword);
        });

    }

    private void signup(String username, String email, String password, String confirmPassword) {
        mProgressDialog.show();
        // Trim leading/trailing spaces from entered credentials
        String trimmedUsername = username.trim();
        String trimmedEmail = email.trim();
        String trimmedPassword = password.trim();
        String trimmedConfirmPassword = confirmPassword.trim();

        if (!trimmedEmail.endsWith("@kcau.ac.ke")) {
            mProgressDialog.dismiss();
            Toast.makeText(SignupActivity.this, "Only valid KCA email addresses are allowed.", Toast.LENGTH_SHORT).show();
            return;
        }
        userReference.orderByChild("username").equalTo(trimmedUsername).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Toast.makeText(SignupActivity.this,"Username already exists. Please choose a different username", Toast.LENGTH_SHORT);
                } else {
                    if (!trimmedPassword.equals(trimmedConfirmPassword)) {
                        Toast.makeText(SignupActivity.this,"Passwords do not match", Toast.LENGTH_SHORT);
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    // Inside your signup process:
                    try {
                        progressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
                                .addOnCompleteListener(this, task1 -> {
                                    progressBar.setVisibility(View.GONE);

                                    if (task1.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String userId = user.getUid();

                                        User newUser = new User(userId, trimmedUsername, trimmedEmail, "clients");

                                        mProgressDialog.dismiss();
                                        Toast.makeText(SignupActivity.this,"Signup successful. Welcome to GroceriesMart", Toast.LENGTH_SHORT);
                                        Toast.makeText(SignupActivity.this,"Kindly login again to complete the registration process", Toast.LENGTH_SHORT);

                                    } else {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(SignupActivity.this,"Account with similar account present.", Toast.LENGTH_SHORT);
                                        Toast.makeText(SignupActivity.this,"Please try to login with existing credentials.", Toast.LENGTH_SHORT);
                                        Toast.makeText(SignupActivity.this,"Or use a different Email \uD83D\uDE42 ", Toast.LENGTH_SHORT);
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(SignupActivity.this,"An error occurred. Please try again.", Toast.LENGTH_SHORT);
            }
        });
    }
}