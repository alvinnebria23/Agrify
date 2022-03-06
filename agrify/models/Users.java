package com.example.agrify.models;

public class Users {
    private String userId;
    private String userName;
    private String userLocation;
    private String userNumber;

    public Users(String userId, String userName, String userLocation, String userNumber) {
        this.userId = userId;
        this.userName = userName;
        this.userLocation = userLocation;
        this.userNumber = userNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public String getUserNumber() {
        return userNumber;
    }


}
