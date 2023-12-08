package com.example.LostFound.Models;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;
    private String conversationId;
    private String status;
    private boolean isTyping;
    public Message() {
        // Empty constructor required by Firebase
    }

    public Message(String messageId, String senderId, String receiverId, String message,
                   long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;

        this.status = status;
    }

    public String getMessageId() {
        return messageId;
    }
    public Boolean getisTyping() {
        return isTyping;
    }
    public void setisTyping(Boolean isTyping) {
        this.isTyping = isTyping;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}