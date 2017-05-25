package com.codecool.shop.controller;

import com.codecool.shop.dao.implementation.ProductDaoJdbc;

import spark.Request;
import spark.Response;
import spark.ModelAndView;

import java.util.HashMap;
import java.util.List;

/**
 * This class is responsible for actions related to products.
 */
public class ProductController {

    /**
     * Renders the products on the homepage.
     * @param req The request
     * @param res The response
     * @return The rendered index page full of products
     */
    public static ModelAndView renderAllProducts(Request req, Response res) {
        ProductDaoJdbc productDataStore = ProductDaoJdbc.getInstance();
        HashMap<String, List> params = new HashMap<>();
        params.put("products", productDataStore.getAll());
        return new ModelAndView(params, "product/index");
    }
}
