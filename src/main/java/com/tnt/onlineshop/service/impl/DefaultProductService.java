package com.tnt.onlineshop.service.impl;

import com.tnt.onlineshop.dao.ProductDao;
import com.tnt.onlineshop.entity.Product;
import com.tnt.onlineshop.service.ProductService;

import java.util.List;

public class DefaultProductService implements ProductService {

    private final ProductDao productDao;

    public DefaultProductService(ProductDao productDao){
        this.productDao = productDao;
    }

    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public Product findById(int id) {
        if (id > 0){
            return productDao.findById(id);
        } else {
            throw new RuntimeException("Id is 0 or less", new IllegalArgumentException());
        }
    }

    @Override
    public boolean add(Product product) {
        if (product != null){
            return productDao.add(product);
        } else {
            throw new RuntimeException("Entity Product is null");
        }
    }

    @Override
    public boolean update(Product product) {
        if (product != null){
            return productDao.update(product);
        } else {
            throw new RuntimeException("Entity Product is null");
        }
    }

    @Override
    public boolean remove(int id) {
        if (id > 0){
            return productDao.remove(id);
        } else {
            throw new RuntimeException("Id is 0 or less", new IllegalArgumentException());
        }
    }

}
