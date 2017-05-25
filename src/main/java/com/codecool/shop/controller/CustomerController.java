package com.codecool.shop.controller;

import com.codecool.shop.dbconnection.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.UUID;

/**
 * This class is responsible for actions related to customers.
 */
public class CustomerController extends DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);


    public Boolean loginValidation(String login, String pw) {
        String query = "SELECT username, password FROM customer WHERE username = ? AND password = ? ";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, login);
            statement.setString(2, pw);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                String username = result.getString("username");
                String password = result.getString("password");
                if (login.equals(username) && pw.equals(password)) {
                    logger.debug("Login for user: {} was successful", login);
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.warn("Exception: {}", e.toString());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fetches a user from the database.
     *
     * @param userName Name of user
     * @return Data of the requested user
     */
    public String getUserId(String userName) {
        String query = "SELECT user_id FROM customer WHERE username='" + userName + "';";

        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("user_id");
                logger.debug("UserID for {} was found", userName);
                return name;
            }
        } catch (
                SQLException e) {
            logger.warn("Exception: {}", e.toString());
            e.printStackTrace();
        }
        return "getUserId failed";
    }

    /**
     * Registers a new user and stores the new data.
     *
     * @param name     Name of user
     * @param email    Email of user
     * @param username Requested username
     * @param password Requested password
     * @param address  Address of user
     */
    public void registerUser(String name, String email, String username, String password, String address) {
        String user_id = UUID.randomUUID().toString();

        String query = "INSERT INTO customer (user_id, name, email, username, password, billing_address, shipping_address) VALUES(?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user_id);
            statement.setString(2, name);
            statement.setString(3, email);
            statement.setString(4, username);
            statement.setString(5, password);
            statement.setString(6, address);
            statement.setString(7, address);
            statement.executeQuery();
            logger.debug("Registration for {} was successful", username);
        } catch (SQLException e) {
            logger.warn("Exception: {}", e.toString());
            e.printStackTrace();
        }
    }
}

