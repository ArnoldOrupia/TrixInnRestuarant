package com.example.arnoh.trixinn;

/**
 * Created by ARNOH on 5/6/2018.
 */

public class SubMenu {

    private String name;
    private double price;

    public SubMenu() {
    }

    public SubMenu(String name, double price) {
        this.name = name;
        this.price = price;
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
}
