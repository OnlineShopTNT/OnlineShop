package com.tnt.onlineshop.dao.imp;

import com.tnt.onlineshop.dao.ProductDao;
import com.tnt.onlineshop.dao.imp.mapper.ProductRowMapper;
import com.tnt.onlineshop.entity.Product;
import com.tnt.onlineshop.util.PropertiesReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcProductDaoITest {

    private static final String TRUNCATE_RESTART_IDENTITY = "TRUNCATE products RESTART IDENTITY";
    private static final String INSERT = "INSERT INTO products (name, price, last_modified_time) VALUES(?, ?, ?)";
    private static final String SELECT_ALL = "SELECT id, name, price, last_modified_time FROM products";
    private static final ProductRowMapper rowMapper = new ProductRowMapper();
    private static HikariDataSource dataSource;
    private static ProductDao productDao;
    private static Connection connection;

    @BeforeAll
    void beforeAll() throws SQLException {
        HikariConfig config = new HikariConfig();
        PropertiesReader propertiesReader = new PropertiesReader();
        config.setJdbcUrl(propertiesReader.getProperty("jdbc.url"));
        config.setUsername(propertiesReader.getProperty("jdbc.user"));
        config.setPassword(propertiesReader.getProperty("jdbc.password"));
        config.setDriverClassName(propertiesReader.getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getProperty("jdbc.maximum.pool.size")));
        dataSource = new HikariDataSource(config);
        productDao = new JdbcProductDao(dataSource);
        connection = dataSource.getConnection();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        truncateProducts();
        try (PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
            for (int i = 0; i < 2; i++) {
                Product product = new Product();
                insertStatement.setString(2, "product" + (1 + i));
                insertStatement.setDouble(2, 1. + i);
                insertStatement.setTimestamp(3, Timestamp.from(Instant.now()));
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();
          }
    }

    @AfterEach
    void afterEach() throws SQLException {
        truncateProducts();
    }

    private void truncateProducts() throws SQLException {
        Statement truncateStatement = connection.createStatement();
        truncateStatement.execute(TRUNCATE_RESTART_IDENTITY);
        truncateStatement.close();
    }

    private List<Product> findAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        try (Statement selectStatement = connection.createStatement();
             ResultSet resultSet = selectStatement.executeQuery(SELECT_ALL)) {
            while (resultSet.next()) {
                Product product = rowMapper.mapRow(resultSet);
            }
        }
        return products;
    }

    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }

    @Test
    void add() {
    }

    @Test
    void update() {
    }

    @Test
    void remove() {
    }

}