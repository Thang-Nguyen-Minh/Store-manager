package com.example.OOP.models;
// File: Product.java
public class Product {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private String category;

    // Constructor
    public Product(String id, String name, double price, int quantity, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    // Getter and Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Methods
    public void addStock(int quantity) {
        this.quantity += quantity;
    }

    public void removeStock(int quantity) {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
        } else {
            System.out.println("Không đủ số lượng sản phẩm trong kho.");
        }
    }

    public void updatePrice(double newPrice) {
        this.price = newPrice;
    }

    public String getInfo() {
        return String.format("Mã sản phẩm: %s\nTên: %s\nGiá: %.2f\nSố lượng: %d\nLoại: %s",
                id, name, price, quantity, category);
    }
}

