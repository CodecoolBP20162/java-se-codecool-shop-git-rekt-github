package com.codecool.shop.controller;

import com.codecool.shop.dbconnection.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderController extends DBConnection {

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

    private void insertOrderData(String user_ID, Integer quantity, Integer prodID) {
        String query = "INSERT INTO orders (user_id, quantity, status, product_id) VALUES('" + user_ID + "'," +
                quantity + ",'paid'," + prodID + ")";
        executeQuery(query);
    }

    private void deleteCart(String userid) {
        String query = "DELETE FROM cart WHERE user_id = '" + userid + "'";
        executeQuery(query);
    }
}
