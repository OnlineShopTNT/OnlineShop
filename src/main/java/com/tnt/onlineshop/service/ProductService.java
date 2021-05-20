package com.tnt.onlineshop.service;

import com.tnt.onlineshop.entity.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAll();

    Product findById(int id);

    boolean add(Product product);

    boolean update(Product product);

    boolean remove(int id);

}
