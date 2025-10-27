package com.example.prm392_cinema.Models;

import java.io.Serializable;

public class Product implements Serializable {
    private int productId;
    private String productName;
    private String description;
    private double price;
    private String productType;
    private int quantity;
    private String imageUrl;
    private int selectedQuantity = 0;

    public Product(int productId, String productName, String description, double price, String productType, int quantity, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.productType = productType;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getProductType() { return productType; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
    public int getSelectedQuantity() { return selectedQuantity; }

    // Setters
    public void setProductId(int productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setProductType(String productType) { this.productType = productType; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setSelectedQuantity(int selectedQuantity) { this.selectedQuantity = selectedQuantity; }
}
