package com.tnt.onlineshop.dao;

import com.tnt.onlineshop.entity.Product;

import java.util.List;

public interface ProductDao {

    List<Product> findAll();

    Product findById(int id);

    boolean add(Product product);

    boolean update(Product product);

    boolean remove(int id);

}
