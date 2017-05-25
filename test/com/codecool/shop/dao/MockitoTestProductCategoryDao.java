package com.codecool.shop.dao;


import com.codecool.shop.model.ProductCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MockitoTestProductCategoryDao {

    @Mock private ProductCategoryDao dao;
    @Mock private ProductCategory category1;
    @Mock private ProductCategory category2;

    @Test
    public void testFind() {
        Mockito.when(dao.find(1)).thenReturn(category1);
        assertEquals(dao.find(1), category1);
    }

    @Test
    public void testGetAll() {
        List<ProductCategory> categoryList = new ArrayList<ProductCategory>(Arrays.asList(category1, category2));
        Mockito.when(dao.getAll()).thenReturn(categoryList);
        assertEquals(dao.getAll(), categoryList);
    }


}
