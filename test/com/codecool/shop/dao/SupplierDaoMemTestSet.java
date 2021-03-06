package com.codecool.shop.dao;

import com.codecool.shop.dao.implementation.SupplierDaoMem;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupplierDaoMemTestSet extends SupplierDaoTest {

    @BeforeAll
    public static void setup() {
        dao = SupplierDaoMem.getInstance();
        Supplier amazon = new Supplier("Amazon", "Digital content and services");
        dao.add(amazon);
        Supplier lenovo = new Supplier("Lenovo", "Computers and laptops");
        dao.add(lenovo);
    }

    @Override
    @Test
    public void testIsSupplierDaoSingleton() {
        List<Supplier> daoList = new ArrayList<>(dao.getAll());
        SupplierDao fakeDao = SupplierDaoMem.getInstance();
        List<Supplier> fakeList = new ArrayList<>(fakeDao.getAll());
        assertEquals(daoList, fakeList);
    }

    @Override
    @Test
    public void testIsGetAllWorking() {
        assertEquals(3, dao.getAll().size());
    }
}
