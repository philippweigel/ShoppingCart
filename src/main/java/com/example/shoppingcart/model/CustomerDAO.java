package com.example.shoppingcart.model;

import com.example.shoppingcart.util.DBConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    public static ObservableList<Customer> getCustomers() {
        ObservableList<Customer> customers =
                FXCollections.observableArrayList();
        Connection con;
        Customer customer;

        try {
            con = DBConnector.connect();
            String sql = "SELECT * FROM Customer";
            ResultSet rs = con.createStatement().executeQuery(sql);

            while (rs.next()) {
                customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                customers.add(customer);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return customers;
    }
}
