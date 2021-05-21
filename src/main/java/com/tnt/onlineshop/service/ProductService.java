package com.tnt.onlineshop.service;

import com.tnt.onlineshop.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> findAll();

    Optional<Product> findById(int id);

    boolean add(Product product);

    boolean update(Product product);

    boolean remove(int id);

}
