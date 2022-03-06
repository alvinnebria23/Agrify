package com.example.agrify.ListOfDemandersPackage;

public class Demanders {
    private String profilePic;
    private int userID;
    private String fullName;
    private String location;
    private String contactNumber;
    private int demandID;
    private int price;
    private String varietyName;
    private int neededKilograms;
    private int receivedKilograms;
    private String durationEnd;
    private String description;
    private String productName;
    private String agriculturalSector;

    public Demanders(String profilePic, int userID, String fullName, String location, String contactNumber, int demandID, int price, String varietyName, int neededKilograms, int receivedKilograms, String durationEnd, String description, String productName, String agriculturalSector) {
        this.profilePic = profilePic;
        this.userID = userID;
        this.fullName = fullName;
        this.location = location;
        this.contactNumber = contactNumber;
        this.demandID = demandID;
        this.price = price;
        this.varietyName = varietyName;
        this.neededKilograms = neededKilograms;
        this.receivedKilograms = receivedKilograms;
        this.durationEnd = durationEnd;
        this.description = description;
        this.productName = productName;
        this.agriculturalSector = agriculturalSector;
    }

    public String getProductName() {
        return productName;
    }

    public String getAgriculturalSector() {
        return agriculturalSector;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public int getUserID() {
        return userID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getLocation() {
        return location;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public int getDemandID() {
        return demandID;
    }

    public int getPrice() {
        return price;
    }

    public String getVarietyName() {
        return varietyName;
    }

    public int getNeededKilograms() {
        return neededKilograms;
    }

    public int getReceivedKilograms() {
        return receivedKilograms;
    }

    public String getDurationEnd() {
        return durationEnd;
    }

    public String getDescription() {
        return description;
    }
}
