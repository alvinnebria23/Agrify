package com.example.agrify.AddDemandPackage;

public class Product {
    private String productID, productName;

    public Product(String productID, String productName) {
        this.productID = productID;
        this.productName = productName;
    }

    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }
}
