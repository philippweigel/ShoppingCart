package com.example.shoppingcart.model;

import com.example.shoppingcart.util.DBConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemDAO {

    public static ObservableList<Item> getItems() {
        ObservableList<Item> items =
                FXCollections.observableArrayList();
        Connection con;
        Item item;

        try {
            con = DBConnector.connect();
            String sql = "SELECT * FROM Item JOIN Category ON Item.category_id = Category.id";
            ResultSet rs = con.createStatement().executeQuery(sql);

            while (rs.next()) {
                item = new Item(
                        rs.getInt("Item.id"),
                        rs.getString("Item.name"),
                        rs.getDouble("Item.price"),
                        rs.getString("Category.name")
                );
                items.add(item);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return items;
    }
}
