package com.codecool.shop.dao;

import com.codecool.shop.dao.implementation.ProductCategoryDaoJdbc;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.model.ProductCategory;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class MockitoTestCategoryJdbcAndMem {

    @Mock private static ProductCategoryDaoJdbc jdbc = mock(ProductCategoryDaoJdbc.class);
    @Mock private static ProductCategoryDaoMem mem = mock(ProductCategoryDaoMem.class);
    private ProductCategory category1 = new ProductCategory("name1", "dep1", "desc1");
    private ProductCategory category2 = new ProductCategory("name2", "dept2", "desc2");
    private ArrayList<ProductCategory> categories = new ArrayList<>(Arrays.asList(category1, category2));

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testGetAll(ProductCategoryDao argument) {
        Mockito.when(argument.getAll()).thenReturn(categories);
        assertEquals(categories, argument.getAll());
    }

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testFind(ProductCategoryDao argument) {
        Mockito.when(argument.find(0)).thenReturn(category1);
        assertEquals(category1, argument.find(0));
    }

    @Test
    void testGetCategory() {
        Mockito.when(jdbc.getCategory(0)).thenReturn(category1);
        assertEquals(category1, jdbc.getCategory(0));
    }

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testAdd(ProductCategoryDao argument) {
        ProductCategory tablet = new ProductCategory("Tablet", "Hardware", "A tablet computer, commonly shortened to tablet, is a thin, flat mobile computer with a touchscreen display.");
        argument.add(tablet);
        Mockito.when(argument.find(argument.getAll().size())).thenReturn(tablet);
        assertEquals(tablet, argument.find(argument.getAll().size()));
    }

    static Stream<ProductCategoryDao> daoAndMem() {
        return Stream.of(mem, jdbc);
    }
}
