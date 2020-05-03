package com.example.wineonadime;

public class FavoriteItem {
    private String name;
    private double price;
    private String brand;

    public FavoriteItem() {

    }

    public FavoriteItem(String name, double price, String brand) {
        this.name = name;
        this.price = price;
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getBrand() {
        return brand;
    }
}
