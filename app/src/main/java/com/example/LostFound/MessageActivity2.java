package com.example.LostFound;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import androidx.annotation.NonNull;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LostFound.Models.Message;
import com.example.LostFound.Models.User;
import com.example.LostFound.adapters.MessageAdapter2;

import com.google.firebase.database.ChildEventListener;
import com.squareup.picasso.Picasso;

public class MessageActivity2 extends AppCompatActivity {
    private ImageView sellerProfileImage;
    private ImageView backgroundImageView;
    private TextView sellerUsernameText;
    private String senderUserId;
    private ChildEventListener typingIndicatorListener;
    private RecyclerView recyclerView;
    private MessageAdapter2 messageAdapter;
    private List<Message> messageList;
    private ValueEventListener messagesListener;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private Uri imageUri;
    private EditText editTextMessage;
    private ImageView buttonSend;
    private ImageView buttonAttachImage;
    private DatabaseReference messagesReference;
    private DatabaseReference conversationsReference;

    private String currentUserId;
    private String senderUsername;
    private String currentUsername;
    private LinearLayout typingIndicatorLayout;
    private DatabaseReference typingIndicatorReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the activity to always use night mode
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recycler_gchat);
        typingIndicatorLayout = findViewById(R.id.typingIndicatorLayout);
        sellerProfileImage = findViewById(R.id.sellerProfileImage);
        sellerUsernameText = findViewById(R.id.sellerUsernameText);
        backgroundImageView=findViewById(R.id.backgroundImageView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Set up the message list
        messageList = new ArrayList<>();

        // Set up the Firebase database reference
        messagesReference =
                FirebaseDatabase.
                        getInstance("https://lost-and-found-project-99acb-default-rtdb.firebaseio.com/")
                .getReference().child("messages");
        conversationsReference = FirebaseDatabase.getInstance().
                getReference().child("conversations");

        currentUserId = getCurrentUserId();
        senderUsername = getIntent().getStringExtra("currentUsername");

        // Retrieve current username
        getCurrentUsername(new UsernameCallback() {
            @Override
            public void onUsernameReceived(String username) {
                if (username != null) {
                    currentUsername = username;

                    List<Message> messagesList = new ArrayList<>(); // Replace this with your actual implementation to populate the list of messages
                    Context context = MessageActivity2.this; // Replace with the appropriate Context instance

                    String currentUsername = username; // Replace this with your actual current user ID retrieval logic

                    messageAdapter = new MessageAdapter2(context, messageList, currentUsername);
                    recyclerView.setAdapter(messageAdapter);

                    retrieveSenderUserId();


                    // ...
                } else {
                    // Handle the case where the username is null
                    // Show an error message or take appropriate action
                }
            }
        });

        // Set up the EditText and Button
        editTextMessage = findViewById(R.id.edit_gchat_message);
        buttonSend = findViewById(R.id.button_gchat_send);

        // Send button click listener
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUsername != null) {
                    String messageText = editTextMessage.getText().toString().trim();
                    if (!messageText.isEmpty()) {
                        sendMessage(messageText, currentUsername);
                        editTextMessage.setText("");
                        // Hide the soft keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);

                    } else {
                        // Handle the case where the message text is empty
                        // Show an error message or take appropriate action
                    }
                } else {
                    // Handle the case where the username is null
                    // Show an error message or take appropriate action
                }
            }
        });



        // Set up the image attachment button
        buttonAttachImage = findViewById(R.id.button_gchat_attach_image);
        buttonAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageAttachmentOptions();
            }
        });

        // Initialize the typing indicator
        typingIndicatorReference = FirebaseDatabase.getInstance("https://havencaremedical-default-rtdb.firebaseio.com/")
                .getReference().child("typingIndicator");
        typingIndicatorListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Implement the onChildAdded method here
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        typingIndicatorReference.addChildEventListener(typingIndicatorListener);
    }

    private void showImageAttachmentOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attach Image");
        builder.setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dispatchTakePhotoIntent();
                        break;
                    case 1:
                        dispatchChooseFromGalleryIntent();
                        break;
                }
            }
        });
        builder.show();
    }

    // Add the following methods to handle image capture and selection
    private void dispatchTakePhotoIntent() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to save the captured image
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(MessageActivity2.this, "com.example.LostFound.fileprovider", photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            imageUri = Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    private void dispatchChooseFromGalleryIntent() {
        Intent chooseFromGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseFromGalleryIntent, REQUEST_IMAGE_PICK);
    }

    // Override the onActivityResult() method to handle the result of image capture or selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    // Image captured from camera
                    // Use the captured image URI (imageUri) for further processing
                    // Example: Upload image to Firebase Storage
                    uploadImageToFirebaseStorage(imageUri);
                    break;
                case REQUEST_IMAGE_PICK:
                    // Image selected from gallery
                    // Use the selected image URI (data.getData()) for further processing
                    // Example: Upload image to Firebase Storage
                    uploadImageToFirebaseStorage(data.getData());
                    break;
            }
        }
    }
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            byte[] imageData = baos.toByteArray();

            // Upload the compressed image data to Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String imageFileName = "images/" + UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageRef.child(imageFileName);

            // Start the upload
            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    // Retrieve the download URL of the uploaded image
                    // Inside the `onSuccess` method where you retrieve the image URL
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            String imageUrl = downloadUri.toString();
                            String senderId = currentUsername; // Assuming you have the sender ID available here
                            sendImage(imageUrl, senderId);
                        }
                        // Send image method
                        private void sendImage(String imageUrl, String senderId) {
                            DatabaseReference newMessageReference = messagesReference.child(currentUsername).push();
                            String messageId = newMessageReference.getKey();
                            long timestamp = System.currentTimeMillis();

                            Message message = new Message(messageId, senderId, senderUsername, imageUrl, timestamp);
                            newMessageReference.setValue(message);
                        }

                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@androidx.annotation.NonNull Exception e) {
                    // Handle any errors that occurred during the upload
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Handle the case where the current user is null or not authenticated
            return null;
        }
    }

    private void getCurrentUsername(final UsernameCallback callback) {
        String currentUserId = getCurrentUserId();

        DatabaseReference usersReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child("Admin").child(currentUserId);
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        String currentUsername = user.getUsername();
                        callback.onUsernameReceived(currentUsername);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("MessageActivity", "Error retrieving current username: " + databaseError.getMessage());
            }
        });
    }

    private interface UsernameCallback {
        void onUsernameReceived(String currentUsername);
    }

    private void retrieveSenderUserId() {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");
        Query query = usersReference.orderByChild("username").equalTo(senderUsername);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        senderUserId = userSnapshot.getKey();
                    }
                } else {
                    // Handle the case where the senderUsername does not exist
                    Toast.makeText(MessageActivity2.this, "Sender User ID not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
                Log.e("MessageActivity", "Error retrieving sender user ID: " + databaseError.getMessage());
            }
        });
    }

    private void retrieveConversations(String conversationId) {
        if (conversationId != null) {
            DatabaseReference conversationRef = messagesReference.child(conversationId);

            conversationRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    // Retrieve the message data from the snapshot
                    Message message = dataSnapshot.getValue(Message.class);

                    // Add the message to your messageList or update your UI as needed
                    if (message != null) {
                        List<Message> messagesList = new ArrayList<>(); // Replace this with your actual implementation to populate the list of messages
                        Context context = MessageActivity2.this; // Replace with the appropriate Context instance

                        //  String currentUserId = getCurrentUserId(); // Replace this with your actual current user ID retrieval logic
                        messageList.add(message);
                        messageAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    // Handle the case where a child message has been updated
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    // Handle the case where a child message has been removed
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    // Handle the case where a child message has been moved
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error case
                    Log.e("MessageActivity", "Error retrieving conversations: " + databaseError.getMessage());
                }
            });
        } else {
            // Handle the case where conversationId is null
            Toast.makeText(MessageActivity2.this, "Conversation ID is null", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendMessage(String messageText, String currentUsername) {
        DatabaseReference newMessageReference = messagesReference.child(currentUsername).push();
        String messageId = newMessageReference.getKey();
        long timestamp = System.currentTimeMillis();


        Message message = new Message(messageId, currentUsername, senderUsername, messageText, timestamp);
        newMessageReference.setValue(message);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Check if the listener reference is not null before removing the listener
        if (typingIndicatorListener != null) {
            // Remove the typing indicator listener
            DatabaseReference typingIndicatorRef = FirebaseDatabase.getInstance().getReference("typingIndicator");
            typingIndicatorRef.removeEventListener(typingIndicatorListener);
        }
    }


}