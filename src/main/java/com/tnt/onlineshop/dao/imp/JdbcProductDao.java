package com.tnt.onlineshop.dao.imp;

import com.tnt.onlineshop.dao.ProductDao;
import com.tnt.onlineshop.entity.Product;

import java.util.List;

public class JdbcProductDao implements ProductDao {

    @Override
    public List<Product> findAll() {
        return null;
    }

    @Override
    public Product findById(int id) {
        return null;
    }

    @Override
    public boolean add(Product product) {
        return false;
    }

    @Override
    public boolean update(Product product) {
        return false;
    }

    @Override
    public boolean remove(int id) {
        return false;
    }

}
