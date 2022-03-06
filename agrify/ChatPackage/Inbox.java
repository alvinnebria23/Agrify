package com.example.agrify.ChatPackage;

public class Inbox {
    private String conversationID;
    private String userID;
    private String profilePic;
    private String fullName;
    private String message;
    private String dateTimeSent;
    private String status;
    private String receiverID;

    public Inbox(String conversationID, String userID, String profilePic, String fullName, String message, String dateTimeSent, String status, String receiverID) {
        this.conversationID = conversationID;
        this.userID = userID;
        this.profilePic = profilePic;
        this.fullName = fullName;
        this.message = message;
        this.dateTimeSent = dateTimeSent;
        this.status = status;
        this.receiverID = receiverID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public String getConversationID() {
        return conversationID;
    }

    public String getUserID() {
        return userID;
    }

    public String getProfilePic() {
        return profilePic;
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

    public String getStatus() {
        return status;
    }
}
