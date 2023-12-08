package com.example.LostFound;

//import static androidx.constraintlayout.core.motion.MotionPaths.TAG;

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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.LostFound.Models.Message;
import com.example.LostFound.Models.User;
import com.example.LostFound.adapters.MessageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

//import id.zelory.compressor.Compressor;
//import id.zelory.compressor.constraint.Compression;
//import id.zelory.compressor.constraint.DefaultConstraint;
////import id.zelory.compressor.constraint.Sampling;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
//
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//import id.zelory.compressor.Compressor;
//
//import java.io.File;
//import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView sellerProfileImage;
    private TextView sellerUsernameText;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private LinearLayout typingIndicatorLayout;
    private DatabaseReference typingIndicatorReference;
    private DatabaseReference sellersRef;
    private ChildEventListener typingIndicatorListener;
    private ImageView buttonSend;
    private RelativeLayout layout;
    private DatabaseReference messagesReference;
    private DatabaseReference conversationsReference;
    private ValueEventListener messagesListener;
    // Add these member variables to the MessageActivity class
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private Uri imageUri; // Stores the URI of the attached image
    private ImageView buttonAttachImage;
    private String currentUserId;
    private String sellerUsername;
    private String currentUsername;

    private Context context;
    private ImageView backgroundImageView;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);// Light theme layout

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recycler_gchat);
        typingIndicatorLayout = findViewById(R.id.typingIndicatorLayout);
        sellerProfileImage=findViewById(R.id.sellerProfileImage);
        sellerUsernameText=findViewById(R.id.sellerUsernameText);
        backgroundImageView=findViewById(R.id.backgroundImageView);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Set up the message list
        messageList = new ArrayList<>();

        // Set up the Firebase database reference
        messagesReference = FirebaseDatabase.getInstance("https://lost-and-found-project-99acb-default-rtdb.firebaseio.com/").getReference().child("messages");
        conversationsReference = FirebaseDatabase.getInstance().getReference().child("conversations");
        typingIndicatorReference = FirebaseDatabase.getInstance().getReference("typingIndicator");
        sellersRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Admins");

        // Get the current user ID and seller username
        currentUserId = getCurrentUserId();
        sellerUsername="admin";
        productId = getIntent().getStringExtra("productId");

        getCurrentUsername(currentUserId, new MessageActivity.UsernameCallback() {
            @Override
            public void onUsernameReceived(String username) {
                currentUsername = username;

                // Set up the RecyclerView for messages
                recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                messageList = new ArrayList<>();
                messageAdapter = new MessageAdapter(context, messageList, currentUsername) {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return null;
                    }
                };

                recyclerView.setAdapter(messageAdapter);


                // Send a message when the send button is clicked
                buttonSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message = editTextMessage.getText().toString().trim();
                        String senderId = currentUsername; // Replace with the actual sender ID
                        String messageText= "For LostProductId : "+productId+message;
                        sendMessage(messageText, senderId);
                    }
                });

                // Enable or disable the send button based on the message input
                editTextMessage.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().trim().isEmpty()) {
                            buttonSend.setEnabled(false);
                        } else {
                            buttonSend.setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

                // Set up the typing indicator listener
                // Set up the typing indicator listener
                typingIndicatorListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                        // Implement the onChildAdded method here
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                        if (!dataSnapshot.getKey().equals(currentUserId)) {
                            showTypingIndicator();
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.getKey().equals(currentUserId)) {
                            hideTypingIndicator();
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };

            }

        });

        // Retrieve current username
        getCurrentUsername(new UsernameCallback() {
            @Override
            public void onUsernameReceived(String username) {
                if (username != null) {
                    currentUsername = username;

                    // Set up the message adapter
                    messageAdapter = new MessageAdapter(MessageActivity.this, messageList, currentUsername) {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return null;
                        }
                    };
                    recyclerView.setAdapter(messageAdapter);

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
                    String senderId = currentUsername; // Use the current username as the sender ID
                    String messageText = editTextMessage.getText().toString().trim();
                    if (!messageText.isEmpty()) {
                        sendMessage(messageText, senderId);
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

//        Inside the onCreate() method, add the following code// Attach image button click listener
        buttonAttachImage = findViewById(R.id.button_gchat_attach_image);
        buttonAttachImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageAttachmentOptions();
            }
        });
    }
    // Add the following method to handle image attachment options
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

    // Add the following methods to handle image capture and selection
    private void dispatchTakePhotoIntent() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to save the captured image
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(MessageActivity.this, "com.example.LostFound.fileprovider", photoFile);
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

                        }
                        // Send image method


                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
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

    private void getCurrentUsername(final MessageActivity.UsernameCallback callback) {
        String currentUserId = getCurrentUserId();

        DatabaseReference usersReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child("Clients").
                child(currentUserId);
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


    private void sortMessagesByTimestamp() {
        Collections.sort(messageList, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return Long.compare(o1.getTimestamp(), o2.getTimestamp());
            }
        });
    }
    public void getCurrentUsername(String currentUserId, UsernameCallback callback) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String username = user.getUsername();
                    callback.onUsernameReceived(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }


    private void sendMessage(String messageText, String senderId) {
        DatabaseReference newMessageReference = messagesReference.child(currentUsername).push();
        String messageId = newMessageReference.getKey();
        long timestamp = System.currentTimeMillis();

        // Show typing indicator
        showTypingIndicator();

        Message message = new Message(messageId, senderId, sellerUsername, messageText, timestamp);
        newMessageReference.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Hide typing indicator on successful message sending
                    hideTypingIndicator();
                    editTextMessage.setText("");
                } else {
                    // Handle failed message sending
                    // You can display an error message or handle it as per your app's logic
                }
            }
        });
    }
    private void showTypingIndicator() {
        typingIndicatorLayout.setVisibility(View.VISIBLE);
    }

    private void hideTypingIndicator() {
        typingIndicatorLayout.setVisibility(View.GONE);
    }

}