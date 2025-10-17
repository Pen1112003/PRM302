package com.example.prm392_cinema.Models;

public class Fab {
    private int foodId;
    private String name;
    private String description;
    private int price;

    //custom prop
    private int quantity;

    public Fab(int foodId, String name, String description, int price) {
        this.foodId = foodId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = 0;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
