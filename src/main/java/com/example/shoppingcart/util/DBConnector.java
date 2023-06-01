package com.example.shoppingcart.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {

    private static Connection conn;

    static {
        try {
            InputStream is = DBConnector.class.getClassLoader().getResourceAsStream("config.properties");
            Properties properties = new Properties();
            if (is != null) {
                properties.load(is);
                String dbHost = properties.getProperty("db.host");
                String dbPort = properties.getProperty("db.port");
                String dbName = properties.getProperty("db.name");
                String dbUser = properties.getProperty("db.user");
                String dbPassword = properties.getProperty("db.password");

                String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
                    conn = DriverManager.getConnection(url, dbUser, dbPassword);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    System.err.println("Error loading MySQL Driver: " + e.getMessage());
                }
            } else {
                throw new RuntimeException("Failed to find config.properties file");
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    public static Connection connect() throws SQLException {
        if (conn != null && !conn.isClosed())
            return conn;
        else
            throw new SQLException("Failed to establish a database connection");
    }
}