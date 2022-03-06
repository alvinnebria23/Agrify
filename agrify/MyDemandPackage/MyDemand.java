package com.example.agrify.MyDemandPackage;

public class MyDemand {
    private String productImage;
    private int demandID;
    private String agriculturalSector;
    private String productName;
    private String productVarietyName;
    private int vendorID;
    private int price;
    private int neededKilograms;
    private int receivedKilograms;
    private String dateDemanded;
    private String durationEnd;
    private String description;
    private String status;

    public MyDemand(String productImage, int demandID, String agriculturalSector, String productName, String productVarietyName, int vendorID, int price, int neededKilograms, int receivedKilograms, String dateDemanded, String durationEnd, String description, String status) {
        this.productImage = productImage;
        this.demandID = demandID;
        this.agriculturalSector = agriculturalSector;
        this.productName = productName;
        this.productVarietyName = productVarietyName;
        this.vendorID = vendorID;
        this.price = price;
        this.neededKilograms = neededKilograms;
        this.receivedKilograms = receivedKilograms;
        this.dateDemanded = dateDemanded;
        this.durationEnd = durationEnd;
        this.description = description;
        this.status = status;
    }

    public String getProductImage() {
        return productImage;
    }

    public int getDemandID() {
        return demandID;
    }

    public String getAgriculturalSector() {
        return agriculturalSector;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductVarietyName() {
        return productVarietyName;
    }

    public int getVendorID() {
        return vendorID;
    }

    public int getPrice() {
        return price;
    }

    public int getNeededKilograms() {
        return neededKilograms;
    }

    public int getReceivedKilograms() {
        return receivedKilograms;
    }

    public String getDateDemanded() {
        return dateDemanded;
    }

    public String getDurationEnd() {
        return durationEnd;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }
}
