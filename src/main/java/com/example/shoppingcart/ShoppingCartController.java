package com.example.shoppingcart;

import com.example.shoppingcart.model.*;
import com.example.shoppingcart.util.DBConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.Map;
import java.util.ResourceBundle;

public class ShoppingCartController implements Initializable {

    // The combobox for selecting customers
    @FXML
    private ComboBox<Customer> customerComboBox;

    // The field for searching items
    @FXML
    private TextField itemSearchField;
    // The table for displaying items
    @FXML
    private TableView<Item> itemTable;

    // The columns in the item table
    @FXML
    private TableColumn<Item, String> nameColumn;
    @FXML
    private TableColumn<Item, String> categoryColumn;
    @FXML
    private TableColumn<Item, Double> priceColumn;

    // The list of items that will be displayed in the item table
    private ObservableList<Item> items;

    // The field for inputting the quantity of an item to add to the cart
    @FXML
    private TextField quantityField;

    // The object representing the cart and its items
    private OrderItem orderItem = new OrderItem();

    // The table for displaying the items in the cart
    @FXML
    private TableView<OrderItem> orderItemTable;

    // The list of order items that will be displayed in the order item table
    private ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();

    // The columns in the order item table
    @FXML
    private TableColumn<OrderItem, String> orderItemNameColumn;
    @FXML
    private TableColumn<OrderItem, Integer> orderItemQuantityColumn;
    @FXML
    private TableColumn<OrderItem, Double> orderItemPriceColumn;

    // The label for displaying the total price of the items in the cart
    @FXML
    private Label totalPriceLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate the customer combobox with customers from the database
        customerComboBox.setItems(CustomerDAO.getCustomers());

        // Set up the cell value factories for the item table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Populate the item table with items from the database
        items = ItemDAO.getItems();
        itemTable.setItems(items);

        // Set up the cell value factories for the order item table
        orderItemNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        orderItemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        orderItemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        // Configure the price column in the order item table to display prices with two decimal places
        orderItemPriceColumn.setCellFactory(column -> new TableCell<OrderItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);

                if (price == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", price));
                }
            }
        });

    }
    @FXML
    public void onSearchButtonClick(ActionEvent actionEvent) {
        // Get the search query from the search field
        String searchQuery = itemSearchField.getText().toLowerCase();

        // Filter the items based on the search query
        ObservableList<Item> filteredItems= items.filtered(item ->
                item.getName().toLowerCase().contains(searchQuery) ||
                        item.getCategory().toLowerCase().contains(searchQuery)
        );

        // Update the item table with the filtered items
        itemTable.setItems(filteredItems);

    }
    @FXML
    public void onAddToCartButtonClick(ActionEvent actionEvent) {

        // Get the selected item from the item table
        Item selectedItem = itemTable.getSelectionModel().getSelectedItem();

        // Check if the quantity field is not null and not empty
        if(quantityField.getText() != null && !quantityField.getText().trim().isEmpty()){
            int quantity = Integer.parseInt(quantityField.getText());

            // Check if a valid item is selected and the quantity is greater than 0
            if(selectedItem != null && quantity > 0){
                // Add the selected item to the cart with the specified quantity
                orderItem.addItem(selectedItem,quantity);

                //Update orderItems
                orderItems.clear();
                for (Map.Entry<Item, Integer> entry : orderItem.getItems().entrySet()) {
                    orderItems.add(new OrderItem(entry.getKey(), entry.getValue()));
                }

                // Update the order item table with the updated order items
                orderItemTable.setItems(orderItems);

                //Update total price
                updateTotalPrice();

            }
        }

    }
    // This method updates the total price label based on the items in the cart
    public void updateTotalPrice(){
        double totalPrice = 0;

        for(OrderItem item: orderItems){
            totalPrice += item.getTotalPrice();
        }

        totalPriceLabel.setText(String.format("%.2f", totalPrice));
    }

    public void onCheckoutButtonClick(ActionEvent actionEvent) {
        try (Connection connection = DBConnector.connect()) {
            // Insert the order into the orders table
            String insertOrderQuery = "INSERT INTO `order` (customer_id) SELECT id FROM customer WHERE name = ?";
            try (PreparedStatement orderStatement = connection.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
                orderStatement.setString(1, selectedCustomer.getName()); // assuming customerComboBox is a ComboBox<String> for customer selection
                orderStatement.executeUpdate();

                // Get the ID of the inserted order
                int orderId;
                try (ResultSet generatedKeys = orderStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }

                // Insert the cart items into the order_items table
                String insertItemsQuery = "INSERT INTO order_item (item_id, order_id, quantity) VALUES (?,?,?)";
                try (PreparedStatement itemsStatement = connection.prepareStatement(insertItemsQuery)) {
                    for (OrderItem item : orderItems) {
                        itemsStatement.setInt(1, item.getItem().getId());
                        itemsStatement.setInt(2, orderId);
                        itemsStatement.setInt(3, item.getQuantity());
                        itemsStatement.executeUpdate();
                    }
                }
            }

            // Clear the cart and the displayed tables
            System.out.println("Order added!");
            orderItems.clear();
            updateTotalPrice();

        } catch (SQLException e) {
            // Handle the exception
            e.printStackTrace();
        }

    }

    public void onRemoveFromCartButtonClick(ActionEvent actionEvent) {
        // Get the selected item from the order item table
        OrderItem selectedItem = orderItemTable.getSelectionModel().getSelectedItem();

        if(selectedItem != null){
            // Remove the selected item from the cart and the order item table
            orderItem.removeItem(selectedItem.getItem());
            orderItemTable.getItems().remove(selectedItem);

            // Update the total price
            updateTotalPrice();
        }

    }


}