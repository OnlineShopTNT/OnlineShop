package com.tnt.onlineshop.dao.imp;

import com.tnt.onlineshop.dao.ProductDao;
import com.tnt.onlineshop.dao.imp.mapper.ProductRowMapper;
import com.tnt.onlineshop.entity.Product;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProductDao implements ProductDao {

    private static final ProductRowMapper productRowMapper = new ProductRowMapper();
    private static final String SELECT_ALL = "SELECT id, name, price, last_modified_time FROM products";
    private static final String FIND_BY_ID = "SELECT id, name, price, last_modified_time FROM products WHERE id = ?";
    private static final String INSERT = "INSERT INTO products (name, price, last_modified_time) VALUES(?, ?, ?)";
    private static final String UPDATE_BY_ID = "UPDATE products SET name = ?, price = ?, last_modified_time = ? WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM products WHERE id = ?";
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
    public Optional<Product> findById(int id) {
        Optional<Product> productOptional = Optional.empty();
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement findByIdStatement = connection.prepareStatement(FIND_BY_ID)) {
            findByIdStatement.setInt(1, id);
            try (ResultSet resultSet = findByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    Product product = productRowMapper.mapRow(resultSet);
                    productOptional = Optional.of(product);
                    if (resultSet.next()) {
                        throw new RuntimeException("More than one user was found for id=" + id);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding product by id in db " + id, e);
        }
        return productOptional;
    }

    @Override
    public boolean add(Product product) {
        boolean isAdded = false;
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement addStatement = connection.prepareStatement(INSERT)) {
            addStatement.setString(1, product.getName());
            addStatement.setDouble(2, product.getPrice());
            addStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            int resultInsert = addStatement.executeUpdate();
            if (resultInsert > 0) {
                isAdded = true;
            }
        } catch (PSQLException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while adding product to db, product: " + product, e);
        }
        return isAdded;
    }

    @Override
    public boolean update(Product product) {
        boolean isUpdated = false;
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE_BY_ID)) {
            updateStatement.setString(1, product.getName());
            updateStatement.setDouble(2, product.getPrice());
            updateStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            updateStatement.setInt(4, product.getId());
            int resultUpdate = updateStatement.executeUpdate();
            if (resultUpdate > 0) {
                isUpdated = true;
            }
        } catch (PSQLException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while updating product in db, product: " + product, e);
        }
        return isUpdated;
    }

    @Override
    public boolean delete(int id) {
        boolean isDeleted = false;
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(DELETE_BY_ID)) {
            deleteStatement.setInt(1, id);
            int amountDeleted = deleteStatement.executeUpdate();
            if (amountDeleted > 0) {
                isDeleted = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting product from db by id=" + id, e);
        }
        return isDeleted;
    }
}
