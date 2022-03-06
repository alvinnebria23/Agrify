package com.example.agrify.models;

public class Product {
    private String productId;
    private String productName;
    private double productPrice;
    private double productRating;
    private String productCategory;
    private String date;
    private String productStocks;
    private String productDescription;
    private String productImage1;
    private String productImage2;
    private String productImage3;
    private String productImage4;
    private String productStockId;
    private String productFavorateId;


    public Product(String productId, String category){
        this.productId = productId;
        this.productCategory = category;

    }
    public Product(String productId, String productName, String category, double productPrice,String productStocks,double productRating,String productDescription,String productImage1,String productImage2,String productImage3,String productImage4,String productStockId){
        this.productId = productId;
        this.productName = productName;
        this.productCategory = category;
        this.productPrice = productPrice;
        this.productStocks = productStocks;
        this.productRating = productRating;
        this.productDescription = productDescription;
        this.productImage1 = productImage1;
        this.productImage2 = productImage2;
        this.productImage3 = productImage3;
        this.productImage4 = productImage4;
        this.productStockId = productStockId;

    }
    public Product(String productId, String productName, String category, double productPrice,String productStocks,double productRating,String productDescription,String productImage1,String productImage2,String productImage3,String productImage4,String productStockId,String productFavorateId){
        this.productId = productId;
        this.productName = productName;
        this.productCategory = category;
        this.productPrice = productPrice;
        this.productStocks = productStocks;
        this.productRating = productRating;
        this.productDescription = productDescription;
        this.productImage1 = productImage1;
        this.productImage2 = productImage2;
        this.productImage3 = productImage3;
        this.productImage4 = productImage4;
        this.productStockId = productStockId;
        this.productFavorateId = productFavorateId;

    }
    public Product(String productName, int i, double productPrice, String date) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.date = date;
    }

    public String getProductId(){ return  productId;}

    public String getProductStocks() {
        return productStocks;
    }

    public String getDate() {
        return date;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public double getProductRating() {
        return productRating;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductImage1() {
        return productImage1;
    }

    public String getProductImage2() {
        return productImage2;
    }

    public String getProductImage3() {
        return productImage3;
    }

    public String getProductImage4() {
        return productImage4;
    }


    public String getProductStockId() {
        return productStockId;
    }

    public String getProductFavorateId() {
        return productFavorateId;
    }
}
