package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dbconnection.DBConnection;
import com.codecool.shop.model.Supplier;

import java.sql.*;
import java.util.ArrayList;

/**
 * This is a DAO for the suppliers. It contains methods which handle objects related to it from a database.
 */
public class SupplierDaoJdbc extends DBConnection implements SupplierDao {

    private static ArrayList<Supplier> suppliers = new ArrayList<>();
    private static SupplierDaoJdbc instance = null;

    private SupplierDaoJdbc() {
    }

    /**
     * @see ProductCategoryDaoMem#getInstance()
     * @return The one and only instance of this class
     */
    public static SupplierDaoJdbc getInstance() {
        if (instance == null) {
            instance = new SupplierDaoJdbc();
        }
        return instance;
    }

    /**
     * Fetches every supplier and related data.
     *
     * @return Suppliers as objects
     */
    @Override
    public ArrayList<Supplier> getAll() {
        suppliers.clear();
        String query = "SELECT * FROM supplier;";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Supplier supplier = new Supplier(Integer.parseInt(resultSet.getString("id")), resultSet.getString("name"), "asdasd");
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    /**
     * Fetches a supplier from the memory.
     *
     * @param id ID of supplier
     * @return The supplier as an object
     */
    static Supplier getSupplier(String id) {
        Supplier result = null;
        for (Supplier supplier : suppliers) {
            if (supplier.getId() == Integer.parseInt(id)) {
                result = supplier;
            }
        }
        return result;
    }

    /**
     * Adds a new supplier to the database.
     *
     * @param supplier The supplier as an object
     */
    @Override
    public void add(Supplier supplier) {
        int newId = suppliers.size() + 1;
        String query = "INSERT INTO supplier (id, name) VALUES (" + newId + ",'" + supplier.getName() + "');";
        executeQuery(query);
    }

    /**
     * Finds the requested supplier.
     *
     * @param id ID of supplier
     * @return The supplier as an object
     */
    @Override
    public Supplier find(int id) {
        String query = "SELECT * FROM supplier WHERE id=" + id + ";";
        Supplier foundSupplier = null;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                foundSupplier = new Supplier(Integer.parseInt(resultSet.getString("id")), resultSet.getString("name"), "");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundSupplier;
    }
}
