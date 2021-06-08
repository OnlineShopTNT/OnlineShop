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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcProductDaoITest {

    private static final String TRUNCATE_RESTART_IDENTITY = "TRUNCATE products RESTART IDENTITY";
    private static final String INSERT = "INSERT INTO products (name, price, last_modified_time) VALUES(?, ?, ?)";
    private static final String SELECT_ALL = "SELECT id, name, price, last_modified_time FROM products";
    private static final ProductRowMapper rowMapper = new ProductRowMapper();
    private HikariDataSource dataSource;
    private ProductDao productDao;
    private Connection connection;
    private Timestamp insertTime;

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
                insertStatement.setString(1, "product" + (1 + i));
                insertStatement.setDouble(2, 1. + i);
                insertStatement.setTimestamp(3, Timestamp.from(Instant.now()));
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();
            insertTime = Timestamp.from(Instant.now());
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
                products.add(product);
            }
        }
        return products;
    }

    @Test
    void findAll() {
        //when
        List<Product> actualProducts = productDao.findAll();
        //then
        assertEquals(2, actualProducts.size());
        assertNotEquals(actualProducts.get(0), actualProducts.get(1));
    }

    @Test
    void findAllEmptyTable() throws SQLException {
        //prepare
        truncateProducts();
        //when
        List<Product> actualProducts = productDao.findAll();
        //then
        assertTrue(actualProducts.isEmpty());
    }

    @Test
    void findById() {
        //when
        Optional<Product> actualProductOptional = productDao.findById(1);
        //then
        assertTrue(actualProductOptional.isPresent());
        Product actualProduct = actualProductOptional.get();
        assertEquals(1, actualProduct.getId());
        assertEquals("product1", actualProduct.getName());
        assertEquals(1., actualProduct.getPrice());
    }

    @Test
    void findByNonExistentId() {
        //when
        Optional<Product> actualProductOptional = productDao.findById(3);
        //then
        assertFalse(actualProductOptional.isPresent());
    }

    @Test
    void add() throws SQLException {
        //prepare
        Product product = new Product();
        product.setName("product3");
        product.setPrice(3.);
        //when
        boolean isAdded = productDao.add(product);
        //then
        assertTrue(isAdded);
        List<Product> actualProducts = findAllProducts();
        product.setId(3);
        assertEquals(3, actualProducts.size());
        assertTrue(actualProducts.contains(product));
        assertTrue(actualProducts.get(2).getLastModifiedTime().compareTo(actualProducts.get(1).getLastModifiedTime()) > 0);
    }

    @Test
    void addNullName() throws SQLException {
        //when
        boolean isAdded = productDao.add(new Product());
        //then
        assertFalse(isAdded);
        List<Product> actualProducts = findAllProducts();
        assertEquals(2, actualProducts.size());
    }

    @Test
    void update() throws SQLException {
        //prepare
        Product product = new Product();
        product.setId(2);
        product.setName("product2modified");
        product.setPrice(2.5);
        //when
        boolean isUpdated = productDao.update(product);
        //then
        assertTrue(isUpdated);
        List<Product> actualProducts = findAllProducts();
        assertEquals(2, actualProducts.size());
        assertEquals(product, actualProducts.get(1));
        assertTrue(actualProducts.get(1).getLastModifiedTime().compareTo(insertTime) > 0);
    }

    @Test
    void updateProductNullName() throws SQLException {
        //prepare
        Product product = new Product();
        product.setId(2);
        product.setName(null);
        product.setPrice(2);
        //when
        boolean isUpdated = productDao.update(product);
        //then
        assertFalse(isUpdated);
        List<Product> actualProducts = findAllProducts();
        assertEquals(2, actualProducts.size());
        assertFalse(actualProducts.contains(product));
    }

    @Test
    void updateProductNonExistentId() throws SQLException {
        //prepare
        Product product = new Product();
        product.setId(3);
        product.setName("product3modified");
        product.setPrice(3.5);
        //when
        boolean isUpdated = productDao.update(product);
        //then
        assertFalse(isUpdated);
        List<Product> actualProducts = findAllProducts();
        assertEquals(2, actualProducts.size());
        assertFalse(actualProducts.contains(product));
    }

    @Test
    void delete() throws SQLException {
        //when
        boolean isDeleted = productDao.delete(2);
        //then
        assertTrue(isDeleted);
        List<Product> actualProducts = findAllProducts();
        assertEquals(1, actualProducts.size());
    }

    @Test
    void deleteNonExistentId() throws SQLException {
        //when
        boolean isDeleted = productDao.delete(3);
        //then
        assertFalse(isDeleted);
        List<Product> actualProducts = findAllProducts();
        assertEquals(2, actualProducts.size());
    }

}