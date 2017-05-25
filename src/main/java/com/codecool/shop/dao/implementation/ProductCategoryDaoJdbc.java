package com.codecool.shop.dao.implementation;


import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dbconnection.DBConnection;
import com.codecool.shop.model.ProductCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a DAO for the product categories. It contains methods which handle objects related to it from a database.
 */
public class ProductCategoryDaoJdbc extends DBConnection implements ProductCategoryDao {

    private static ArrayList<ProductCategory> categories = new ArrayList<>();
    private static ProductCategoryDaoJdbc instance = null;

    private ProductCategoryDaoJdbc() {
    }

    /**
     * @see ProductCategoryDaoMem#getInstance()
     * @return The one and only instance of this class
     */
    public static ProductCategoryDaoJdbc getInstance() {
        if (instance == null) {
            instance = new ProductCategoryDaoJdbc();
        }
        return instance;
    }

    /**
     * Fetches every product category and related data.
     *
     * @return Product categories as objects
     */
    @Override
    public List<ProductCategory> getAll() {
        categories.clear();
        String query = "SELECT * FROM category;";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                ProductCategory category = new ProductCategory(Integer.parseInt(resultSet.getString("id")), resultSet.getString("name"), resultSet.getString("department"), "asdasd");
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Fetches a given product category based on ID.
     *
     * @param id ID of product category
     * @return The product category as an object
     */
    public ProductCategory getCategory(int id) {
        String categoryId = Integer.toString(id);
        ProductCategory result = null;

        for (ProductCategory category : categories) {
            if (category.getId() == Integer.parseInt(categoryId)) {
                result = category;
            }
        }
        return result;
    }

    /**
     * Finds the requested product category.
     *
     * @param id ID of product category
     * @return The product category as an object
     */
    @Override
    public ProductCategory find(int id) {
        String query = "SELECT * FROM category WHERE id=" + id + ";";
        ProductCategory foundCategory = null;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                foundCategory = new ProductCategory(Integer.parseInt(resultSet.getString("id")), resultSet.getString("name"), resultSet.getString("department"), "asdasd");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundCategory;
    }

    /**
     * Adds a new product category to the database.
     *
     * @param category The product category object
     */
    @Override
    public void add(ProductCategory category) {
        int newId = categories.size() + 1;
        String query = "INSERT INTO category (id, name, department) VALUES (" + newId + ",'" + category.getName() + "','" + category.getDepartment() + "');";
        executeQuery(query);
    }
}