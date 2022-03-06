package com.example.agrify.ChatPackage;

/**
 * Created by Belal on 5/29/2016.
 */
public class Message {
    private String conversationID;
    private String senderID;
    private String fullName;
    private String message;
    private String dateTimeSent;
    private String profilePicture;
    private Boolean isSameSenderID;
    public Message(String conversationID, String senderID, String fullName,
                   String message, String dateTimeSent, String profilePicture , Boolean isSameSenderID) {
        this.conversationID = conversationID;
        this.senderID = senderID;
        this.fullName = fullName;
        this.message = message;
        this.dateTimeSent = dateTimeSent;
        this.profilePicture = profilePicture;
        this.isSameSenderID = isSameSenderID;
    }

    public String getConversationID() {
        return conversationID;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMessage() {
        return message;
    }

    public String getDateTimeSent() {
        return dateTimeSent;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public Boolean getSameSenderID() {
        return isSameSenderID;
    }
}