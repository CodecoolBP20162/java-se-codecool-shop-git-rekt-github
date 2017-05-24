package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.model.Supplier;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a DAO for the suppliers. It stores data inside the memory instead of a database.
 */
public class SupplierDaoMem implements SupplierDao {

    private List<Supplier> DATA = new ArrayList<>();
    private static SupplierDaoMem instance = null;

    private SupplierDaoMem() {
    }

    /**
     * @see ProductCategoryDaoMem#getInstance()
     * @return The one and only instance of this class
     */
    public static SupplierDaoMem getInstance() {
        if (instance == null) {
            instance = new SupplierDaoMem();
        }
        return instance;
    }

    /**
     * Adds a new supplier to the memory.
     *
     * @param supplier The supplier as an object
     */
    @Override
    public void add(Supplier supplier) {
        supplier.setId(DATA.size() + 1);
        DATA.add(supplier);
    }

    /**
     * Finds the requested supplier.
     *
     * @param id ID of supplier
     * @return The supplier as an object
     */
    @Override
    public Supplier find(int id) {
        return DATA.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }


    /**
     * Fetches every supplier and related data.
     *
     * @return Suppliers as objects
     */
    @Override
    public List<Supplier> getAll() {
        return DATA;
    }
}
