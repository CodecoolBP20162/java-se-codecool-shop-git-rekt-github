package com.codecool.shop.controller;

import com.codecool.shop.dbconnection.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is responsible for actions related to orders.
 */
public class OrderController extends DBConnection {

    /**
     * Stores the content of the shopping cart as an order in the database.
     * @param userID ID of user
     */
    public void addCartToOrder(String userID) {
        String query = "SELECT * FROM cart WHERE user_id = '" + userID + "';";

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Integer productID = resultSet.getInt("product_id");
                Integer qty = resultSet.getInt("quantity");
                insertOrderData(userID, qty, productID);
                deleteCart(userID);
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts new data for an order into the database.
     * @param userID ID of user
     * @param quantity Quantity of item
     * @param prodID ID of product
     */
    private void insertOrderData(String userID, Integer quantity, Integer prodID) {
        String query = "INSERT INTO orders (user_id, quantity, status, product_id) VALUES('" + userID + "'," +
                quantity + ",'paid'," + prodID + ")";
        executeQuery(query);
    }

    /**
     * Clears out the content of the shopping cart from the database.
     * @param userID ID of user
     */
    private void deleteCart(String userID) {
        String query = "DELETE FROM cart WHERE user_id = '" + userID + "'";
        executeQuery(query);
    }
}
