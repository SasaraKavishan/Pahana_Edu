package com.pahanaedu.bookshop.model;

public class Item {
    private int itemId;
    private String name;
    private double price;
    private String category;
    private int stock;

    public Item(int itemId, String name, double price, String category, int stock) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}