package com.tnt.onlineshop.dao.imp.mapper;

import com.tnt.onlineshop.dao.RowMapper;
import com.tnt.onlineshop.entity.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet resultSet) {
        Product product = new Product();
        try {
            product.setId(resultSet.getInt("id"));
            product.setName(resultSet.getString("name"));
            product.setPrice(resultSet.getDouble("price"));
            product.setLastModifiedTime(resultSet.getTimestamp("last_modified_time"));
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error while reading product from result set", e);
        }
    }

}
