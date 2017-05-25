package com.codecool.shop.dao;


import com.codecool.shop.dao.implementation.*;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class MockitoTestSupplierDao {

    @Mock private static SupplierDaoJdbc jdbc = mock(SupplierDaoJdbc.class);
    @Mock private static SupplierDaoMem mem = mock(SupplierDaoMem.class);
    @Mock private Supplier mockSupplier1;
    @Mock private Supplier mockSupplier2;
    private ArrayList<Supplier> suppliers;

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testFind(SupplierDao argument) {
        Mockito.when(argument.find(0)).thenReturn(mockSupplier1);
        assertEquals(mockSupplier1, argument.find(0));
    }

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testGetAll(SupplierDao argument) {
        Mockito.when(argument.getAll()).thenReturn(suppliers);
        assertEquals(suppliers, argument.getAll());
    }

    @ParameterizedTest
    @MethodSource(names = "daoAndMem")
    void testAdd(SupplierDao argument) {
        argument.add(mockSupplier2);
        Mockito.when(argument.find(argument.getAll().size())).thenReturn(mockSupplier2);
        assertEquals(mockSupplier2, argument.find(argument.getAll().size()));
    }

    static Stream<SupplierDao> daoAndMem() {
        return Stream.of(jdbc, mem);
    }
}
