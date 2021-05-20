package com.tnt.onlineshop.dao.imp.mapper;

import com.tnt.onlineshop.entity.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ProductRowMapper {

    public Product mapRow(ResultSet resultSet) {
        Product product = new Product();
        try {
            product.setId(resultSet.getInt("id"));
            product.setName(resultSet.getString("name"));
            product.setPrice(resultSet.getDouble("price"));
            product.setLastModifiedTime(resultSet.getObject("last_modified_time", LocalDateTime.class));
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error while reading product from result set", e);
        }
    }

}
