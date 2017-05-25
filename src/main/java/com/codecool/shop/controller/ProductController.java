package com.codecool.shop.controller;

import com.codecool.shop.dao.implementation.ProductDaoJdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.ModelAndView;

import java.util.HashMap;
import java.util.List;

/**
 * This class is responsible for actions related to products.
 */
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    /**
     * Renders the products on the homepage.
     *
     * @param req The request
     * @param res The response
     * @return The rendered index page full of products
     */
    public static ModelAndView renderAllProducts(Request req, Response res) {
        ProductDaoJdbc productDataStore = ProductDaoJdbc.getInstance();
        HashMap<String, List> params = new HashMap<>();
        params.put("products", productDataStore.getAll());
        logger.debug("Product added to {}", params);
        return new ModelAndView(params, "product/index");
    }
}
