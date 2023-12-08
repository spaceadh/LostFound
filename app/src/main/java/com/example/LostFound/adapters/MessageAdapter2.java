package com.example.LostFound.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LostFound.ImageActivity;
import com.example.LostFound.Models.Message;
import com.example.LostFound.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SENT_MESSAGE = 0;
    private static final int RECEIVED_MESSAGE = 1;

    private Context context;
    private List<Message> messages;
    private FirebaseFirestore firestore;
    private String currentUsername;

    public MessageAdapter2(Context context, List<Message> messages, String currentUsername) {
        this.context = context;
        this.messages = messages;
        this.currentUsername = currentUsername;
        firestore = FirebaseFirestore.getInstance();
        Fresco.initialize(context); // Initialize Fresco
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == SENT_MESSAGE) {
            View sentView = inflater.inflate(R.layout.item_message_x, parent, false);
            return new SentMessageViewHolder(sentView);
        } else {
            View receivedView = inflater.inflate(R.layout.item_message_y, parent, false);
            return new ReceivedMessageViewHolder(receivedView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (getItemViewType(position) == SENT_MESSAGE) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            sentHolder.textMessage.setText(message.getMessage());
            sentHolder.textTimestamp.setText(formatDate(message.getTimestamp()));

            // Handle image messages
            if (isImageUrl(message.getMessage())) {
                sentHolder.imageMessage.setVisibility(View.VISIBLE);
                sentHolder.textMessage.setVisibility(View.GONE);
                sentHolder.imageMessage.setImageURI(Uri.parse(message.getMessage()));
            } else {
                sentHolder.imageMessage.setVisibility(View.GONE);
                sentHolder.textMessage.setVisibility(View.VISIBLE);
                sentHolder.textMessage.setText(message.getMessage());
            }

            // Set click listener for image
            sentHolder.imageMessage.setOnClickListener(v -> {
                openImageActivity(message.getMessage());
            });

            // Set long click listener for message deletion
            sentHolder.itemView.setOnLongClickListener(v -> {
                deleteMessage(message);
                return true;
            });
        } else {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            receivedHolder.textMessage.setText(message.getMessage());
            receivedHolder.textTimestamp.setText(formatDate(message.getTimestamp()));

            // Handle image messages
            if (isImageUrl(message.getMessage())) {
                receivedHolder.imageMessage.setVisibility(View.VISIBLE);
                receivedHolder.textMessage.setVisibility(View.GONE);
                receivedHolder.imageMessage.setImageURI(Uri.parse(message.getMessage()));
            } else {
                receivedHolder.imageMessage.setVisibility(View.GONE);
                receivedHolder.textMessage.setVisibility(View.VISIBLE);
                receivedHolder.textMessage.setText(message.getMessage());
            }

            // Set click listener for image
            receivedHolder.imageMessage.setOnClickListener(v -> {
                openImageActivity(message.getMessage());
            });

            // Set long click listener for message deletion
            receivedHolder.itemView.setOnLongClickListener(v -> {
                deleteMessage(message);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        //Toast.makeText(context, "You are now chatting with : " + message.getReceiverId(), Toast.LENGTH_SHORT).show();
        if (message != null && message.getSenderId() != null && message.getSenderId().equals(currentUsername)) {
            return SENT_MESSAGE;
        } else {
            return RECEIVED_MESSAGE;
        }
    }

    private boolean isImageUrl(String message) {
        return message != null && message.startsWith("https://firebasestorage.googleapis.com");
    }

    private void openImageActivity(String imageUrl) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra("image_url", imageUrl);
        context.startActivity(intent);
    }

    private void deleteMessage(Message message) {
        if (message != null && message.getMessageId() != null) {
            // Check if the current user is the sender of the message
            if (!message.getSenderId().equals(currentUsername)) {
                Toast.makeText(context, "You can only delete your own messages", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Message");
            builder.setMessage("Are you sure you want to delete this message?");
            // Set positive button
            builder.setPositiveButton("Delete", (dialog, which) -> {
                // Perform deletion logic here
                firestore.collection("messages").document(message.getMessageId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Remove the message from the list
                            int position = messages.indexOf(message);
                            if (position != -1) {
                                messages.remove(position);
                                notifyItemRemoved(position);
                            }
                            Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show();
                        });
            });
            // Set negative button
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // Dismiss the dialog
                dialog.dismiss();
            });
            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }

    private static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textTimestamp;
        SimpleDraweeView imageMessage;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
            textTimestamp = itemView.findViewById(R.id.textView_timestamp);
            imageMessage = itemView.findViewById(R.id.image_message);
        }
    }

    private static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textTimestamp;
        SimpleDraweeView imageMessage;

        private TextView textView_timestamp; // Make sure this line is added

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message);
            textTimestamp = itemView.findViewById(R.id.textView_timestamp);
            imageMessage = itemView.findViewById(R.id.image_message);
        }
    }
}