package com.tnt.onlineshop.dao.imp;

import com.tnt.onlineshop.dao.UserDao;
import com.tnt.onlineshop.dao.imp.mapper.UserRowMapper;
import com.tnt.onlineshop.util.PropertiesReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoTest {

    private static final String TRUNCATE_RESTART_IDENTITY = "TRUNCATE users RESTART IDENTITY";
    private static final String INSERT = "INSERT INTO users (email, iterations, salt, hash) VALUES(?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT id, email, iterations, salt, hash FROM users";
    private static final UserRowMapper rowMapper = new UserRowMapper();
    private static HikariDataSource dataSource;
    private static UserDao userDao;
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
        userDao = new JdbcUserDao(dataSource);
        connection = dataSource.getConnection();
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        truncateUsers();
        try (PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
            for (int i = 1; i < 3; i++) {
                insertStatement.setString(1, "name" + i);
                insertStatement.setInt(2, 10000);
                insertStatement.setString(3, "qweasdzxc" + i);
                insertStatement.setString(4, "qwewrre134234efwf" + i);
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();
        }
    }

    @AfterEach
    void afterEach() throws SQLException {
        truncateUsers();
    }

    private void truncateUsers() throws SQLException {
        Statement truncateStatement = connection.createStatement();
        truncateStatement.execute(TRUNCATE_RESTART_IDENTITY);
        truncateStatement.close();
    }


}