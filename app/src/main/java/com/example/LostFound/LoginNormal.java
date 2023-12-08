package com.example.LostFound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginNormal extends AppCompatActivity {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mSignInButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the activity to always use night mode
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_normal);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.
                getInstance("https://lost-and-found-project-99acb-default-rtdb.firebaseio.com/");

        mUsernameEditText = findViewById(R.id.username_input);
        mPasswordEditText = findViewById(R.id.password_input);
        mSignInButton = findViewById(R.id.sign_in_button);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsernameEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();

                signIn(username, password);
            }
        });

        TextView signUpTextView = findViewById(R.id.sign_up_text);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginNormal.this, SignupActivity.class);
                startActivity(signUpIntent);
            }
        });
    }

    private void signIn(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginNormal.this, "Sign In successful. Welcome back to Lostn`Found", Toast.LENGTH_SHORT);
                                                    updateUserRole(user);
                        } else {
                            // Sign in failed, display a message to the user
                            Toast.makeText(LoginNormal.this, "Sign-in failed. Please check your credentials ", Toast.LENGTH_SHORT);
                            //Toast.makeText(LoginNormal.this, "Sign-in failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserRole(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();

        DatabaseReference usersRef = mDatabase.getReference("Users");

        usersRef.child("Admin").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User is an admin
                    //Toast.makeText(LoginNormal.this, "Welcome back to Lostn`Found, Admin", Toast.LENGTH_SHORT).show();

                    Toast.makeText(LoginNormal.this, "Welcome back Admin", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginNormal.this, AdminDashboard.class));
                    finish(); 
                } else {
                    usersRef.child("Clients").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                startActivity(new Intent(LoginNormal.this, WelcomeClientActivity.class));
                                finish(); // Finish the current activity
                            } else {
                                // Handle other roles or scenarios
                                Toast.makeText(LoginNormal.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                                // Toast.makeText(LoginNormal.this, "Unknown user role", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle the error case if the database operation is cancelled
                            Toast.makeText(LoginNormal.this, "Database operation cancelled.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error case if the database operation is cancelled
                Toast.makeText(LoginNormal.this, "Database operation cancelled.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}