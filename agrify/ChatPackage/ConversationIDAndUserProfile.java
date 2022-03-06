package com.example.agrify.ChatPackage;

public class ConversationIDAndUserProfile {
    private String conversationID;
    private String profilePic;
    private String fullName;
    private String userID;

    public ConversationIDAndUserProfile(String conversationID, String profilePic, String fullName, String userID) {
        this.conversationID = conversationID;
        this.profilePic = profilePic;
        this.fullName = fullName;
        this.userID = userID;
    }

    public String getConversationID() {
        return conversationID;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserID() {
        return userID;
    }
}
