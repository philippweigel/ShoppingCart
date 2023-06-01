package com.example.shoppingcart.model;

import java.util.Date;

public class Order {
    private Date orderDate;


    public Order(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
