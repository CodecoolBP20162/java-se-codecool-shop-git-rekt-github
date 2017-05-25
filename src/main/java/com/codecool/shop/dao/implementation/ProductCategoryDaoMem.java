package com.codecool.shop.dao.implementation;

import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.model.ProductCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a DAO for the product categories. It stores data inside the memory instead of a database.
 */
public class ProductCategoryDaoMem implements ProductCategoryDao {

    private List<ProductCategory> DATA = new ArrayList<>();
    private static ProductCategoryDaoMem instance = null;

    private ProductCategoryDaoMem() {
    }

    /**
     * Singleton checker method.
     *
     * @return The one and only instance of this class
     */
    public static ProductCategoryDaoMem getInstance() {
        if (instance == null) {
            instance = new ProductCategoryDaoMem();
        }
        return instance;
    }

    /**
     * Adds a new product category to the memory.
     *
     * @param category Product category object
     */
    @Override
    public void add(ProductCategory category) {
        category.setId(DATA.size() + 1);
        DATA.add(category);
    }

    /**
     * Finds the requested product category.
     *
     * @param id ID of product category
     * @return The product category as an object
     */
    @Override
    public ProductCategory find(int id) {
        return DATA.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    /**
     * Fetches every product category and related data.
     *
     * @return Product categories as objects
     */
    @Override
    public List<ProductCategory> getAll() {
        return DATA;
    }
}
