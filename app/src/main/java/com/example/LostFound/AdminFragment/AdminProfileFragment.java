package com.example.LostFound.AdminFragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.LostFound.DeleteAccountActivity;
import com.example.LostFound.LogoutActivity;
import com.example.LostFound.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersReference;
    private ImageView profileImageView;
    private TextView usernameTextView;

    public AdminProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view;
        view = inflater.inflate(R.layout.activity_settings, container, false);

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference("Users/Admins");

        // Initialize views
        usernameTextView = view.findViewById(R.id.UserProfileView);
        profileImageView = view.findViewById(R.id.circleImageView);

        // Retrieve the user ID from Firebase
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Retrieve the username and profile picture URL from the database
            usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (isAdded() && dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String profilePictureUrl = dataSnapshot.child("wallpaperChoice").getValue(String.class);

                        // Set the username
                        usernameTextView.setText(username);

                        // Load and set the profile picture using Glide or any other image loading library
                        Glide.with(requireContext())
                                .load(profilePictureUrl)
                                .into(profileImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }

        RelativeLayout logoutLinear = view.findViewById(R.id.logoutLinear);
        logoutLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for "Log Out" section
                openLogoutActivity();
            }
        });


        return view;
    }

    private void openLogoutActivity() {
        Intent intent = new Intent(requireContext(), LogoutActivity.class);
        startActivity(intent);
    }

    void showToast(String message, int duration) {
        Toast toast = Toast.makeText(requireContext(), message, duration);

        View toastView = LayoutInflater.from(requireContext()).inflate(R.layout.toast_layout, null);
        TextView tvMessage = toastView.findViewById(R.id.tvMessage);
        ImageView tvImage = toastView.findViewById(R.id.toast_icon);
        tvMessage.setText(message);

        Glide.with(requireContext())
                .load(R.drawable.output2)
                .into(tvImage);

        toast.setView(toastView);
        toast.show();
    }
}
