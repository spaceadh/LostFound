package com.example.LostFound.Models;

public class ConversationItem {
    private String username;
    private String profilePictureUrl;

    public ConversationItem(String username, String profilePictureUrl) {
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}