package com.tnt.onlineshop.service.impl;

import com.tnt.onlineshop.dao.ProductDao;
import com.tnt.onlineshop.entity.Product;
import com.tnt.onlineshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultProductServiceTest {

    private final Product product = new Product();
    private final ProductDao mockedProductDao = mock(ProductDao.class);
    private final ProductService productService = new DefaultProductService(mockedProductDao);

    @BeforeEach
    void beforeEach() {
        reset(mockedProductDao);
    }

    @Test
    void findByValidIdTest() {
        //prepare
        when(mockedProductDao.findById(1)).thenReturn(java.util.Optional.of(product));
        //when
        Optional<Product> actualOptional = productService.findById(1);
        //then
        assertEquals(Optional.of(product), actualOptional);
        verify(mockedProductDao).findById(1);
    }

    @Test
    void findByNotValidIdTest() {
        //when
        assertThrows(RuntimeException.class, () -> productService.findById(-1));
        //then
        verify(mockedProductDao, never()).findById(-1);
    }

    @Test
    void addTest() {
        //prepare
        when(mockedProductDao.add(product)).thenReturn(true);
        //when
        boolean actualIsAdded = productService.add(product);
        //then
        assertTrue(actualIsAdded);
        verify(mockedProductDao).add(product);
    }

    @Test
    void addNullProductTest() {
        //when
        assertThrows(RuntimeException.class, () -> productService.add(null));
        //then
        verify(mockedProductDao, never()).add(null);
    }

    @Test
    void updateTest() {
        //prepare
        when(mockedProductDao.update(product)).thenReturn(true);
        //when
        boolean actualIsUpdated = productService.update(product);
        //then
        assertTrue(actualIsUpdated);
        verify(mockedProductDao).update(product);
    }

    @Test
    void updateNullProductTest() {
        //when
        assertThrows(RuntimeException.class, () -> productService.update(null));
        //then
        verify(mockedProductDao, never()).update(null);
    }

    @Test
    void deleteValidIdTest() {
        //prepare
        when(mockedProductDao.delete(1)).thenReturn(true);
        //when
        boolean isDeleted = productService.delete(1);
        //then
        assertTrue(isDeleted);
        verify(mockedProductDao).delete(1);
    }

    @Test
    void deleteNotValidIdTest() {
        //when
        assertThrows(RuntimeException.class, () -> productService.delete(-1));
        //then
        verify(mockedProductDao, never()).delete(-1);
    }

}