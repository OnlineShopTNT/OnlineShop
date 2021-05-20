package com.tnt.onlineshop.dao.imp;

import com.tnt.onlineshop.dao.ProductDao;
import com.tnt.onlineshop.dao.imp.mapper.ProductRowMapper;
import com.tnt.onlineshop.entity.Product;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDao implements ProductDao {

    private static final ProductRowMapper productRowMapper = new ProductRowMapper();
    private static final String SELECT_ALL = "SELECT id, name, price, last_modified_time FROM products";
    private static final String FIND_BY_ID = "SELECT id, name, price, last_modified_time FROM products WHERE id = ?";
    private static final String INSERT_PRODUCT = "INSERT INTO products (name, price, last_modified_time) VALUE (?, ?, ?)";
    private static final String UPDATE_PRODUCT = "UPDATE products SET name = ?, price = ?, last_modified_time = ? WHERE name = ?";
    private static final String REMOVE_PRODUCT = "DELETE FROM products WHERE id = ?";
    private final HikariDataSource hikariDataSource;

    public JdbcProductDao(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public List<Product> findAll() {
        List<Product> productList = new ArrayList<>();
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement findAllStatement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = findAllStatement.executeQuery()) {
            while (resultSet.next()) {
                Product product = productRowMapper.mapRow(resultSet);
                productList.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while select all products from db", e);
        }
        return productList;
    }

    @Override
    public Product findById(int id) {
        Product product = new Product();
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement findByIdStatement = connection.prepareStatement(FIND_BY_ID)) {
            findByIdStatement.setInt(1, id);
            try (ResultSet resultSet = findByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    product = productRowMapper.mapRow(resultSet);
                    if (resultSet.next()) {
                        throw new RuntimeException("More than one user was found for id" + id);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while find product by id in db", e);
        }
        return product;
    }

    @Override
    public boolean add(Product product) {
        boolean isAdded = false;
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement addStatement = connection.prepareStatement(INSERT_PRODUCT)) {
            addStatement.setString(1, product.getName());
            addStatement.setDouble(2, product.getPrice());
            addStatement.setTimestamp(3, product.getLastModifiedTime());
            int resultInsert = addStatement.executeUpdate();
            if (resultInsert > 0){
                isAdded = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while add product to db", e);
        }
        return isAdded;
    }

    @Override
    public boolean update(Product product) {
        boolean isUpdate = false;
        try(Connection connection = hikariDataSource.getConnection();
        PreparedStatement updateStatement = connection.prepareStatement(UPDATE_PRODUCT)){
            updateStatement.setString(1, product.getName());
            updateStatement.setDouble(2, product.getPrice());
            updateStatement.setTimestamp(3, product.getLastModifiedTime());
            int resultUpdate = updateStatement.executeUpdate();
            if (resultUpdate > 0){
                isUpdate = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while update product in db", e);
        }
        return isUpdate;
    }

    @Override
    public boolean remove(int id) {
        boolean isRemoved = false;
        try(Connection connection = hikariDataSource.getConnection();
        PreparedStatement removeStatement = connection.prepareStatement(REMOVE_PRODUCT)){
            removeStatement.setInt(1, id);
            int removeResult = removeStatement.executeUpdate();
            if (removeResult > 0){
                isRemoved = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while remove product from db", e);
        }
        return isRemoved;
    }
}
