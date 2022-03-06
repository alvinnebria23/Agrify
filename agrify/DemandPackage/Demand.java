package com.example.agrify.DemandPackage;

public class Demand {
    private int row_number;
    private int productID;
    private String image;
    private String productName;
    private int overAllDemandToday;
    private int overAllDemandYesterday;
    private String agriculturalSector;

    public Demand(int row_number, int productID, String image, String productName, int overAllDemandToday, int overAllDemandYesterday, String agriculturalSector) {
        this.row_number = row_number;
        this.productID = productID;
        this.image = image;
        this.productName = productName;
        this.overAllDemandToday = overAllDemandToday;
        this.overAllDemandYesterday = overAllDemandYesterday;
        this.agriculturalSector = agriculturalSector;
    }

    public int getProductID() {
        return productID;
    }

    public int getRowNumber() {
        return row_number;
    }

    public String getImage() {
        return image;
    }

    public String getProductName() {
        return productName;
    }

    public int getOverAllDemandToday() {
        return overAllDemandToday;
    }

    public int getOverAllDemandYesterday() {
        return overAllDemandYesterday;
    }

    public String getAgriculturalSector() {
        return agriculturalSector;
    }
}
