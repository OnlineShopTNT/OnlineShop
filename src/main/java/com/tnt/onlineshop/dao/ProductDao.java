package com.tnt.onlineshop.dao;

import com.tnt.onlineshop.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    List<Product> findAll();

    Optional<Product> findById(int id);

    boolean add(Product product);

    boolean update(Product product);

    boolean delete(int id);

}
