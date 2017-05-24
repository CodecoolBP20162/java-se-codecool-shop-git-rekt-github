package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a DAO for the products. It stores data inside the memory instead of a database.
 */
public class ProductDaoMem implements ProductDao {

    private List<Product> DATA = new ArrayList<>();
    private static ProductDaoMem instance = null;

    private ProductDaoMem() {
    }

    /**
     * @see ProductCategoryDaoMem#getInstance()
     * @return The one and only instance of this class
     */
    public static ProductDaoMem getInstance() {
        if (instance == null) {
            instance = new ProductDaoMem();
        }
        return instance;
    }

    /**
     * Adds a new product to the memory.
     *
     * @param product The product as an object
     */
    @Override
    public void add(Product product) {
        product.setId(DATA.size() + 1);
        DATA.add(product);
    }

    /**
     * Finds the requested product.
     *
     * @param id ID of product
     * @return The product as an object
     */
    @Override
    public Product find(int id) {
        return DATA.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    /**
     * Removes products from the memory.
     *
     * @param id ID of object
     */
    @Override
    public void remove(int id) {
        DATA.remove(find(id));
    }

    /**
     * Fetches every product and related data.
     *
     * @return Products as objects
     */
    @Override
    public List<Product> getAll() {
        return DATA;
    }

    /**
     * @see com.codecool.shop.dao.implementation.ProductDaoJdbc#getBy(Supplier)
     */
    @Override
    public List<Product> getBy(Supplier supplier) {
        return DATA.stream().filter(t -> t.getSupplier().equals(supplier)).collect(Collectors.toList());
    }

    /**
     * @see com.codecool.shop.dao.implementation.ProductDaoJdbc#getBy(ProductCategory)
     */
    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        return DATA.stream().filter(t -> t.getProductCategory().equals(productCategory)).collect(Collectors.toList());
    }
}
