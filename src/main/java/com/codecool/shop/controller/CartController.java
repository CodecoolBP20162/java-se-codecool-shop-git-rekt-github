package com.codecool.shop.controller;

import com.codecool.shop.dbconnection.DBConnection;
import com.codecool.shop.model.LineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;


/**
 * This class is responsible for actions related to the shopping cart.
 */
public class CartController extends DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    /**
     * Checks if the requested item(s) if available in the database. If yes, it returns relevant information.
     *
     * @param userId    ID of user
     * @param productID ID of product
     */
    public void checkCartDB(String userId, Integer productID) {
        String query = "SELECT product_id FROM cart WHERE user_id='" + userId + "' AND product_id = " + productID + ";";

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                updateCartDB(userId, productID);
            } else {
                addToCartDB(userId, productID);
            }
            logger.debug("{} user's product: {}", userId, productID);
        } catch (SQLException e) {
            logger.warn("Exception: {}", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * This function gets and adds up the total price of items.
     *
     * @param userID ID of user
     * @return Returns total price in String
     */
    public String getTotalPrice(String userID) {
        String query = "SELECT product.defaultprice * cart.quantity AS total FROM cart JOIN product ON cart.product_id = product.id WHERE cart.user_id = '" + userID + "';";
        Double price = (double) 0;

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                price += resultSet.getDouble("total");
            }
            logger.debug("{} user's total price: {}", userID, price);
        } catch (SQLException e) {
            logger.warn("Exception: {}", e.toString());
            e.printStackTrace();
        }
        return price.toString();
    }

    /**
     * Fetches the content of the shopping cart from the database.
     *
     * @param userID ID of user
     * @return Content of the shopping cart
     */
    public ArrayList<LineItem> getCartContentDB(String userID) {
        ArrayList<LineItem> products = new ArrayList<>();
        String query = "SELECT product.name, product.defaultprice, cart.quantity, product.defaultprice * cart.quantity AS total FROM cart JOIN product ON cart.product_id = product.id WHERE cart.user_id = '" + userID + "';";

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String productName = resultSet.getString("name");
                Integer quantity = resultSet.getInt("quantity");
                Integer defaultPrice = resultSet.getInt("defaultprice");
                products.add(new LineItem(productName, defaultPrice, "USD", quantity));
            }
            logger.debug("{} users cart content was returned", userID);
        } catch (SQLException e) {
            logger.warn("Exception: {}", e.toString());
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Updates a database record with the content of the shopping cart.
     *
     * @param userId    ID of user
     * @param productID ID of product
     */
    private void updateCartDB(String userId, Integer productID) {
        String query = "UPDATE cart SET quantity = quantity + 1 WHERE user_id='" + userId + "' AND product_id ='" + productID + "';";
        executeQuery(query);
        logger.debug("Product: {} was updated in {}'s cart", productID, userId);
    }

    /**
     * Adds the content of the shopping cart to the database.
     *
     * @param userId ID of user
     * @param prodID ID of product
     */
    private void addToCartDB(String userId, Integer prodID) {
        String query = "INSERT INTO cart(user_id, quantity, product_id) VALUES('" + userId + "',1,'" + prodID + "');";
        executeQuery(query);
        logger.debug("Product: {} was added to {}'s cart", prodID, userId);
    }

    /**
     * Updates the quantity of an item in the database.
     *
     * @param userId   ID of user
     * @param prodName Name of product
     * @param quantity Desired quantity
     */
    public void updateQuantityDB(String userId, String prodName, Integer quantity) {
        Integer prodId = getProductId(prodName);
        String updateQuery = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?;";
        String deleteQuery = "DELETE FROM cart WHERE user_id = ? AND product_id = ?;";

        if (quantity == 0) {
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(deleteQuery);
                statement.setString(1, userId);
                statement.setInt(2, prodId);
                statement.executeQuery();
                logger.debug("{} user's quantity was updated", userId);
            } catch (SQLException e) {
                logger.warn("Exception: {}", e.toString());
                e.printStackTrace();
            }
        } else {
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(updateQuery);
                statement.setInt(1, quantity);
                statement.setString(2, userId);
                statement.setInt(3, prodId);
                statement.executeQuery();
                logger.debug("{} user's quantity was updated", userId);
            } catch (SQLException e) {
                logger.warn("Exception: {}", e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     * Fetches the number of items in the shopping cart.
     *
     * @param userID ID of user
     * @return Total number of items in the shopping cart
     */
    public Integer getCartSize(String userID) {
        String query = "SELECT SUM(quantity) AS total_quantity FROM cart WHERE user_id = '" + userID + "';";

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                Integer quantity = resultSet.getInt("total_quantity");
                logger.debug("{} user's cart size: {}", userID, quantity);
                return quantity;
            }
        } catch (SQLException e) {
            logger.warn("Exception: {}", e.toString());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets a product from the database with a given ID.
     *
     * @param productName Name of product
     * @return ID of product
     */
    private Integer getProductId(String productName) {
        String query = "SELECT id FROM product WHERE name = '" + productName + "';";

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                Integer productId = resultSet.getInt("id");
                logger.debug("{} product's id: {}", productName, productId);
                return productId;
            }
        } catch (SQLException e) {
            logger.warn("Exception: {}", e.toString());
            e.printStackTrace();
        }
        return 1;
    }
}
