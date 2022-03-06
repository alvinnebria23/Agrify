package com.example.agrify.ChatPackage;

public class ConversationIDAndUserID {
    private String conversationID;
    private String userID;

    public ConversationIDAndUserID(String conversationID, String userID) {
        this.conversationID = conversationID;
        this.userID = userID;
    }

    public String getConversationID() {
        return conversationID;
    }

    public String getUserID() {
        return userID;
    }
}
