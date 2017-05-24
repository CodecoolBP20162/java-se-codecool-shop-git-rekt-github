package com.codecool.shop.dao;


import com.codecool.shop.dao.implementation.ProductCategoryDaoJdbc;
import com.codecool.shop.dao.implementation.ProductDaoJdbc;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class MockitoTestProductDao {

    @Mock private static ProductDaoJdbc jdbc = mock(ProductDaoJdbc.class);
    @Mock private static ProductDaoMem mem = mock(ProductDaoMem.class);
    @Mock private Product mockProduct1;
    @Mock private Product mockProduct2;
    @Mock private Supplier mockSupplier;
    @Mock private ProductCategory mockCategory;
    private ArrayList<Product> products;

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testFind(ProductDao argument) {
        Mockito.when(argument.find(0)).thenReturn(mockProduct1);
        assertEquals(mockProduct1, argument.find(0));
    }

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testGetAll(ProductDao argument) {
        Mockito.when(argument.getAll()).thenReturn(products);
        assertEquals(products, argument.getAll());
    }

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testAdd(ProductDao argument) {
        argument.add(mockProduct1);
        Mockito.when(argument.find(argument.getAll().size())).thenReturn(mockProduct1);
        assertEquals(mockProduct1, argument.find(argument.getAll().size()));
    }

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testRemove(ProductDao argument) {
        argument.remove(1);
        Mockito.when(argument.find(argument.getAll().size())).thenReturn(null);
        assertEquals(null, argument.find(argument.getAll().size()));
    }

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testGetBySupplier(ProductDao argument) {
        Mockito.when(argument.getBy(mockSupplier)).thenReturn(products);
        assertEquals(products, argument.getBy(mockSupplier));
    }

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testGetByCategory(ProductDao argument) {
        Mockito.when(argument.getBy(mockCategory)).thenReturn(products);
        assertEquals(products, argument.getBy(mockCategory));
    }


    static Stream<ProductDao> daoAndMem() {
        return Stream.of(jdbc, mem);
    }
}
