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
import com.example.LostFound.Models.Message;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SENT_MESSAGE = 0;
    private static final int RECEIVED_MESSAGE = 1;

    private Context context;
    private List<Message> messages;
    private FirebaseFirestore firestore;
    private String currentUsername;

    public MessageAdapter (Context context, List<Message> messages, String currentUsername) {
        this.context = context;
        this.messages = messages;
        this.currentUsername = currentUsername;
        firestore = FirebaseFirestore.getInstance();
        Fresco.initialize(context); // Initialize Fresco
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textTimestamp;
        SimpleDraweeView imageMessage;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message_text);
            textTimestamp = itemView.findViewById(R.id.text_message_timestamp);
            imageMessage = itemView.findViewById(R.id.image_message);
        }
    }

    private static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textTimestamp;
        SimpleDraweeView imageMessage;

        //private TextView textView_timestamp; // Make sure this line is added

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.text_message_text);
            textTimestamp = itemView.findViewById(R.id.text_message_timestamp);
            imageMessage = itemView.findViewById(R.id.image_message);
        }
    }
}