module com.example.shoppingcart {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.shoppingcart to javafx.fxml;
    opens com.example.shoppingcart.model to javafx.fxml;
    exports com.example.shoppingcart;

    exports com.example.shoppingcart.model;
}