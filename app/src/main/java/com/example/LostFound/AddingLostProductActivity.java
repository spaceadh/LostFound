package com.example.LostFound;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.LostFound.Models.LostProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddingLostProductActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button pickImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_wallpapers);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Adding LostProduct...");
        mProgressDialog.setCancelable(false);

        pickImageButton = findViewById(R.id.uploadButton);
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            uploadImageToFirebase(selectedImageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {

        EditText titleEditText = findViewById(R.id.titleEditText);
        String ProductName = titleEditText.getText().toString();

        EditText DescriptionEditText = findViewById(R.id.DescriptionEditText);
        String description =DescriptionEditText.getText().toString();

        EditText ProductColorEditText=findViewById(R.id.ProductColorEditText);
        String color = ProductColorEditText.getText().toString();

        EditText usabilityEditText=findViewById(R.id.usabilityEditText);
        String usability = usabilityEditText.getText().toString();

        EditText finderEditText=findViewById(R.id.finderEditText);
        String finder = finderEditText.getText().toString();

        String productOwner = "null";
        String status="lost";

        if (imageUri != null && !ProductName.isEmpty()
                && !description.isEmpty()
                && !color.isEmpty() ) {
            // Create a reference to the "Wallpapers" folder in Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("LostProduct")
                    .child(UUID.randomUUID().toString()); // Use a random UUID as the image name

            // Upload the image file to Firebase Storage
            UploadTask uploadTask = storageRef.putFile(imageUri);

            // Show a progress dialog or update UI to indicate the upload progress
            ProgressDialog progressDialog = new ProgressDialog(AddingLostProductActivity.this);
            progressDialog.setTitle("Uploading LostProduct");
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mProgressDialog.show();

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    // Update the progress dialog with the current upload progress
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    // Get the download URL of the uploaded image
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            String imageUrl = downloadUrl.toString();
                            saveImageUrlToFirebase(imageUrl, ProductName,description,color,
                                    usability,finder,productOwner,status);

                            progressDialog.dismiss();
                            mProgressDialog.dismiss();// Dismiss the progress dialog
                            Toast.makeText(AddingLostProductActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure to get the download URL
                            Toast.makeText(AddingLostProductActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            progressDialog.dismiss(); // Dismiss the progress dialog
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle the failure to upload the image
                    mProgressDialog.dismiss();
                    progressDialog.dismiss(); // Dismiss the progress dialog
                    Toast.makeText(AddingLostProductActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveImageUrlToFirebase(String imageUrl, String ProductName, String description,
                                        String color, String usability,String finder,
                                        String productOwner,String status)
    {    DatabaseReference wallpapersRef = FirebaseDatabase.getInstance().getReference()
            .child("LostProduct");
        mProgressDialog.show();
        // Generate a unique key for the image
        String imageKey = wallpapersRef.push().getKey();

        // Create a lostProduct object with the image URL and title
        LostProduct lostProduct = 
                new LostProduct(imageKey, imageUrl, ProductName,description,color,
                        usability,finder,productOwner,status);
               
        // Save the lostProduct object under the generated key in the "LostProduct" node
        wallpapersRef.child(imageKey).setValue(lostProduct)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mProgressDialog.dismiss();
                        // LostProduct details saved successfully
                        Toast.makeText(AddingLostProductActivity.this, "LostProduct details saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        // Handle the failure to save the lostProduct details
                        Toast.makeText(AddingLostProductActivity.this, "Failed to save LostProduct details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}