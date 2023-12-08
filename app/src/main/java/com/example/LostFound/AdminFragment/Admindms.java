package com.example.LostFound.AdminFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.LostFound.MessageActivity;
import com.example.LostFound.MessageActivity2;
import com.example.LostFound.Models.ConversationItem;
import com.example.LostFound.Models.Message;
import com.example.LostFound.Models.User;
import com.example.LostFound.R;
import com.example.LostFound.UsernameCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Admindms extends Fragment implements UsernameCallback {

    private ListView messageListView;
    private ArrayAdapter<ConversationItem> messageAdapter;
    private List<ConversationItem> messageList;
    private DatabaseReference conversationsReference;
    private ValueEventListener conversationsListener;

    private String currentUsername;

    public Admindms() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dms, container, false);

        // Set up the ListView
        messageListView = view.findViewById(R.id.listView_messages);
        messageList = new ArrayList<>();

        messageAdapter = new ArrayAdapter<ConversationItem>(requireContext(), R.layout.list_item_conversation, R.id.textView_sender, messageList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View itemView = super.getView(position, convertView, parent);

                TextView textViewSender = itemView.findViewById(R.id.textView_sender);
                //ImageView imageViewProfilePicture = itemView.findViewById(R.id.postUsername);
//                TextView textViewLastMessage = itemView.findViewById(R.id.textView_last_message);
//                TextView textViewTimestamp = itemView.findViewById(R.id.textView_timestamp);

                ConversationItem conversationItem = messageList.get(position);
                String senderUsername = conversationItem.getUsername();
                String profilePictureUrl = conversationItem.getProfilePictureUrl();

                // Set the sender's username
                textViewSender.setText(senderUsername);

//                // Set the profile picture
//                if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
//                    Glide.with(requireContext()).load(profilePictureUrl).into(imageViewProfilePicture);
//                } else {
//                    imageViewProfilePicture.setImageResource(R.drawable.profile);
//                }

//                // Get the last message in the conversation
//                getLastMessage(senderUsername, textViewLastMessage, textViewTimestamp);

                return itemView;
            }
        };

        messageListView.setAdapter(messageAdapter);

        // Set up the Firebase database reference
        conversationsReference = FirebaseDatabase.getInstance().getReference().child("conversations");

        // Get the current username
        getCurrentUsername();

        // Set click listener for ListView items
        messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConversationItem conversationItem = messageList.get(position);
                String senderUsername = conversationItem.getUsername();

                // Start MessageActivity2 and pass the sender username as an extra
                Intent intent = new Intent(requireContext(), MessageActivity2.class);
                intent.putExtra("senderUsername", senderUsername);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getCurrentUsername() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference usersReference =
                    FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child("Admin").child(currentUserId);
            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            String currentUsername = user.getUsername();
                            onUsernameRetrieved(currentUsername);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle the error
                }
            });
        }
    }

    @Override
    public void onUsernameRetrieved(String username) {
        if (username != null) {
            currentUsername = username;
            retrieveConversations();
        }
    }

    private void retrieveConversations() {
        DatabaseReference sellerConversationsReference = conversationsReference.child(currentUsername);

        // Create a query to retrieve conversations in descending order by the latest message timestamp
        Query conversationsQuery = sellerConversationsReference.orderByChild("timestamp");

        // Create a value event listener to fetch conversations
        conversationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String senderId = snapshot.getKey();
                    if (senderId != null) {
                        DatabaseReference buyerReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients").child(senderId);
                        buyerReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user != null) {
                                        String senderUsername = user.getUsername();
                                        String profilePictureUrl = user.getProfilePictureUrl();

                                        // Create a custom object containing both username and profile picture URL
                                        ConversationItem conversationItem = new ConversationItem(senderUsername, profilePictureUrl);
                                        messageList.add(conversationItem);
                                        messageAdapter.notifyDataSetChanged();

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle the error
                            }
                        });
                    }
                }

                // Reverse the messageList to display the latest conversation on top
                Collections.reverse(messageList);

                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        };

        // Attach the listener to the conversations query
        conversationsQuery.addValueEventListener(conversationsListener);
    }

    private void getLastMessage(String senderUsername, TextView textViewLastMessage, TextView textViewTimestamp) {
        DatabaseReference messagesReference = FirebaseDatabase.getInstance().getReference()
                .child("messages").child(currentUsername).child(senderUsername);

        // Create a query to retrieve the last message in the conversation
        Query lastMessageQuery = messagesReference.orderByChild("timestamp").limitToLast(1);

        // Create a value event listener to fetch the last message
        ValueEventListener lastMessageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Message lastMessage = snapshot.getValue(Message.class);
                        if (lastMessage != null) {
                            String messageContent = lastMessage.getMessage();
                            textViewLastMessage.setText(messageContent);

                            // Set the last message timestamp (convert timestamp to desired format)
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            String formattedTimestamp = sdf.format(new Date(lastMessage.getTimestamp()));
                            textViewTimestamp.setText(formattedTimestamp);
                        }
                        // You can update the UI or perform any other necessary action with the last message
                        return;
                    }
                } else {
                    // No messages in the conversation
                    textViewTimestamp.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        };

        // Attach the listener to the last message query
        lastMessageQuery.addListenerForSingleValueEvent(lastMessageListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove the value event listener when the fragment is destroyed
        if (conversationsReference != null && conversationsListener != null) {
            conversationsReference.removeEventListener(conversationsListener);
        }
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
