package com.example.LostFound;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class MyAccountDetails extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersReference;
    private StorageReference profilePicturesReference;
    private ProgressBar progressBar;
    private ImageView profileImageView;
    private ImageView editUserNameImageView;
    private ImageView editPhoneNumberPen;
    private ImageView editBioPen;
    private TextView usernameTextView;
    private TextView usernameTextView2;
    private TextView BioTextView;
    private TextView phoneNumberTextView;
    private EditText changeUsernameEditText;
    private EditText PhoneNumberEditText;
    private EditText BioEditText;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the activity to always use night mode
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_details);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        usersReference = firebaseDatabase.getReference("Users/Clients");
        profilePicturesReference = firebaseStorage.getReference("Buyer_profile_pictures");

        // Initialize views
        progressBar = findViewById(R.id.progressBar);

        usernameTextView = findViewById(R.id.UserProfileView);
        usernameTextView2 =findViewById(R.id.UserProfileView2);
        phoneNumberTextView=findViewById(R.id.PhoneNumberTextView);
        BioTextView=findViewById(R.id.BioTextView);
        profileImageView = findViewById(R.id.circleImageView);
        ImageView editUserProfileImageView = findViewById(R.id.editUserProfile);
        ImageView editUserNameImageView = findViewById(R.id.editUserName);
        ImageView editPhoneNumberPen =findViewById(R.id.editPhoneNumberPen);
        ImageView editBioPen=findViewById(R.id.editBioPen);
        //ImageView editBioPenG = findViewById(R.id.editBioPenG);
        changeUsernameEditText = findViewById(R.id.changeUsernameEditText);
        PhoneNumberEditText=findViewById(R.id.PhoneNumberEditText);
        BioEditText=findViewById(R.id.BioEditText);

        // Set click listeners
        editUserProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        editUserNameImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleUsernameEditMode();
            }
        });
        editPhoneNumberPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePhoneNumberEditMode();
            }
        });
        editBioPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBioEditMode();
            }
        });

        RelativeLayout logoutLinear = findViewById(R.id.logoutLinear);
        logoutLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for "Log Out" section
                openLogoutActivity();
            }
        });

        // Retrieve the user ID from Firebase
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Retrieve the username and profile picture URL from the database
            usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);

                        String wallpaperChoice = dataSnapshot.child("wallpaperChoice").getValue(String.class);

                        String Bio=dataSnapshot.child("description").getValue(String.class);
                        if (Bio != null) {
                            BioTextView.setText(Bio);
                        } else {
                            BioTextView.setText("Tell us about yourself");
                        }

                        Long phoneNumber = dataSnapshot.child("phoneNumber").getValue(Long.class);
                        if (phoneNumber != null) {
                            phoneNumberTextView.setText("" + phoneNumber);
                        } else {
                            phoneNumberTextView.setText("No Phone Number Available");
                        }

                        usernameTextView.setText(username);
                        usernameTextView2.setText(username);

                        changeUsernameEditText.setText(username);

                        // Load and set the profile picture using Glide or any other image loading library
                        Glide.with(MyAccountDetails.this)
                                .load(wallpaperChoice)
                                .into(profileImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }
    void showToast(String koffeeMyAccountDetails, int duration) {
        Toast toast = Toast.makeText(this, koffeeMyAccountDetails, duration);

        View view = LayoutInflater.from(this)
                .inflate(R.layout.toast_layout, null);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        ImageView tvImage = view.findViewById(R.id.toast_icon);
        tvMessage.setText(koffeeMyAccountDetails);

        Glide.with(this)
                .load(R.drawable.output2)
                .into(tvImage);

        toast.setView(view);
        toast.show();
    }
    private void togglePhoneNumberEditMode() {
        int visibility = PhoneNumberEditText.getVisibility();

        if (visibility == View.VISIBLE) {
            String newPhoneNumberString = PhoneNumberEditText.getText().toString().trim();

            if (isPhoneNumberValid(newPhoneNumberString)) {
                long newPhoneNumber = Long.parseLong(newPhoneNumberString);
                updatephoneNumber(newPhoneNumber);
            } else {
                showToast("Invalid phone number. Please enter a valid phone number.", Toast.LENGTH_SHORT);
            }
        } else {
            PhoneNumberEditText.setVisibility(View.VISIBLE);
            //editPhoneNumberPen.setImageResource(R.drawable.send_message_red);
        }
    }

    private void toggleUsernameEditMode() {
        int visibility = changeUsernameEditText.getVisibility();
        if (visibility == View.VISIBLE) {
            String newUsername = changeUsernameEditText.getText().toString().trim();

            if (isUsernameValid(newUsername)) {
                checkUsernameAvailability(newUsername);
            } else {
                showToast("Invalid username. Please enter a username with less than 16 characters.", Toast.LENGTH_SHORT);
            }
        } else {
            changeUsernameEditText.setVisibility(View.VISIBLE);
            //editUserNameImageView.setImageResource(R.drawable.send_message_red);
        }
    }
    private void toggleBioEditMode() {
        int visibility = changeUsernameEditText.getVisibility();
        if (visibility == View.VISIBLE) {
            String newBio = BioEditText.getText().toString().trim();

            if (isDescriptionValid(newBio)) {
                updateBio(newBio);
            } else {
                showToast("Invalid Bio. Please enter a Bio with over 16 characters.", Toast.LENGTH_SHORT);
            }
        } else {
            BioEditText.setVisibility(View.VISIBLE);
            //editBioPen.setImageResource(R.drawable.send_message_red);
        }
    }
    private boolean isUsernameValid(String username) {
        return username.length() <= 16;
    }
    private boolean isPhoneNumberValid(String newphoneNumber) {
        return newphoneNumber.matches("\\d+") && newphoneNumber.length() <= 12;
    }
    private boolean isDescriptionValid(String newBio) {
        return newBio.length() > 16;
    }
    private void checkUsernameAvailability(final String newUsername) {
        usersReference.orderByChild("username").equalTo(newUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    updateUsername(newUsername);
                } else {
                    showToast("Username already exists. Please choose a different username.", Toast.LENGTH_SHORT);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
    private void updatephoneNumber(final long newPhoneNumber) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            usersReference.child(userId).child("phoneNumber").setValue(newPhoneNumber)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                phoneNumberTextView.setText(String.valueOf(newPhoneNumber));
                                phoneNumberTextView.setText(String.valueOf(newPhoneNumber));
                                showToast("Phone Number updated successfully", Toast.LENGTH_SHORT);
                            } else {
                                showToast("Phone Number change unsuccessful.", Toast.LENGTH_SHORT);
                                showToast("Kindly ensure you have access to an internet connection.", Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }
    }
    private void updateUsername(final String newUsername) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            usersReference.child(userId).child("username").setValue(newUsername)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast("Username updated successfully", Toast.LENGTH_SHORT);
                                changeUsernameEditText.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                usernameTextView.setText(newUsername);
                                usernameTextView2.setText(newUsername);
                            } else {
                                showToast("Failed to update username", Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                uploadProfilePicture(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfilePicture(Bitmap bitmap) {
        if (selectedImageUri != null) {
            StorageReference imageReference = profilePicturesReference.child(selectedImageUri.getLastPathSegment());
            imageReference.putFile(selectedImageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            updateProfilePicture(downloadUrl);
                                        }
                                    }
                                });
                            } else {
                                // Handle error
                                showToast("Failed to upload picture", Toast.LENGTH_SHORT);
                                //Toast.makeText(MyAccountDetails.this, "Failed to upload profile picture.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void updateProfilePicture(String wallpaperChoice) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            usersReference.child(userId).child("wallpaperChoice").setValue(wallpaperChoice)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                // Load and set the updated profile picture using Glide or any other image loading library
                                Glide.with(MyAccountDetails.this)
                                        .load(wallpaperChoice)
                                        .into(profileImageView);
                            } else {
                                // Handle error
                                showToast("Failed to update profile picture", Toast.LENGTH_SHORT);
                                //Toast.makeText(MyAccountDetails.this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void updateBio(final String newBio) {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            usersReference.child(userId).child("description").setValue(newBio)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                BioTextView.setText(newBio);
                                BioEditText.setText(newBio);
                                showToast("Bio updated successfully", Toast.LENGTH_SHORT);
                            } else {
                                showToast("Bio change unsuccessful.", Toast.LENGTH_SHORT);
                                showToast("Kindly ensure you have access to an internet connection.", Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }
    }
    private void openLogoutActivity () {
        Intent intent = new Intent(this, LogoutActivity.class);
        startActivity(intent);
    }
}