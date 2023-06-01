package com.example.shoppingcart.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrderItem {

    private Item item;
    private int quantity;
    private Map<Item, Integer> items = new HashMap<>();

    public OrderItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public OrderItem() {
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public void addItem(Item item, int quantity) {
        items.put(item, quantity);

    }

    public Map<Item, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public Double getPrice() {
        return item.getPrice();
    }

    public Double getTotalPrice() {
        return item.getPrice() * quantity;
    }

    public String getName() {
        return item.getName();
    }

    public void removeItem(Item selectedItem) {
        items.remove(selectedItem);
    }
}
