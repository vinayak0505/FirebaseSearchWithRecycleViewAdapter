package com.example.one17;

public class foodItem {
    public String itemName, itemPrice;
    public int available;

    public foodItem() {}

    public foodItem(String itemName, String itemPrice, int available) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.available = available;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public int getAvailable() {
        return available;
    }
}
