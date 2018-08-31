package com.example.arnoh.trixinn;

/**
 * Created by ARNOH on 5/9/2018.
 */

public class Cart {

    private String name;
    private int quantity;
    private double price;

    public Cart() {
    }

    public Cart(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}