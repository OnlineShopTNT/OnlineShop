package com.tnt.onlineshop.dao.imp;

import com.tnt.onlineshop.dao.RowMapper;
import com.tnt.onlineshop.dao.UserDao;
import com.tnt.onlineshop.dao.imp.mapper.UserRowMapper;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.util.PropertiesReader;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JdbcUserDaoITest {

    private static final String TRUNCATE_RESTART_IDENTITY = "TRUNCATE users RESTART IDENTITY";
    private static final String INSERT = "INSERT INTO users (email, iterations, salt, hash) VALUES(?, ?, ?, ?)";
    private static final String SELECT_ALL = "SELECT id, email, iterations, salt, hash FROM users";
    private static final RowMapper<User> ROW_MAPPER = new UserRowMapper();
    private HikariDataSource dataSource;
    private UserDao userDao;

    @BeforeAll
    void beforeAll() {
        HikariConfig config = new HikariConfig();
        PropertiesReader propertiesReader = new PropertiesReader();
        config.setJdbcUrl(propertiesReader.getProperty("jdbc.url"));
        config.setUsername(propertiesReader.getProperty("jdbc.user"));
        config.setPassword(propertiesReader.getProperty("jdbc.password"));
        config.setDriverClassName(propertiesReader.getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getProperty("jdbc.maximum.pool.size")));
        dataSource = new HikariDataSource(config);
        userDao = new JdbcUserDao(dataSource);
    }

    @BeforeEach
    void beforeEach() throws SQLException {
        truncateUsers();
        for (int i = 0; i < 2; i++) {
            User user = getUserByNumber(i + 1);
            insertUser(user);
        }
    }

    @AfterEach
    void afterEach() throws SQLException {
        truncateUsers();
    }

    @AfterAll
    void afterAll() {
        dataSource.close();
    }

    private List<User> findAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement findStatement = connection.createStatement();
             ResultSet resultSet = findStatement.executeQuery(SELECT_ALL)) {
            while (resultSet.next()) {
                User user = ROW_MAPPER.mapRow(resultSet);
                users.add(user);
            }
        }
        return users;
    }

    private void insertUser(User user) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
            insertStatement.setString(1, user.getEmail());
            insertStatement.setInt(2, user.getIterations());
            insertStatement.setString(3, user.getSalt());
            insertStatement.setString(4, user.getHash());
            insertStatement.executeUpdate();
        }
    }

    private void truncateUsers() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement truncateStatement = connection.createStatement()) {
            truncateStatement.execute(TRUNCATE_RESTART_IDENTITY);
        }
    }

    private User getUserByNumber(int number) {
        User user = new User();
        user.setEmail("user" + number + "@email.com");
        user.setIterations(number);
        user.setSalt("salt" + number);
        user.setHash("hash" + number);
        return user;
    }

    @Test
    void add() throws SQLException {
        //prepare
        User expectedUser = getUserByNumber(3);
        expectedUser.setId(3);
        //when
        boolean actualIsAdded = userDao.add(expectedUser);
        //then
        assertTrue(actualIsAdded);
        List<User> actualUsers = findAllUsers();
        assertEquals(3, actualUsers.size());
        assertEquals(expectedUser, actualUsers.get(2));
    }

    @Test
    void addExistingEmail() throws SQLException {
        //prepare
        User userExistingEmail = getUserByNumber(3);
        userExistingEmail.setId(3);
        userExistingEmail.setEmail("user2@email.com");
        //when
        boolean actualIsAdded = userDao.add(userExistingEmail);
        //then
        assertFalse(actualIsAdded);
        List<User> actualUsers = findAllUsers();
        assertEquals(2, actualUsers.size());
        assertFalse(actualUsers.contains(userExistingEmail));
    }

    @Test
    void addNullEmail() throws SQLException {
        //prepare
        User userNullEmail = getUserByNumber(3);
        userNullEmail.setId(3);
        userNullEmail.setEmail(null);
        //when
        boolean actualIsAdded = userDao.add(userNullEmail);
        //then
        assertFalse(actualIsAdded);
        List<User> actualUsers = findAllUsers();
        assertEquals(2, actualUsers.size());
        assertFalse(actualUsers.contains(userNullEmail));
    }

    @Test
    void addExistingSalt() throws SQLException {
        //prepare
        User userExistingSalt = getUserByNumber(3);
        userExistingSalt.setId(3);
        userExistingSalt.setSalt("salt2");
        //when
        boolean actualIsAdded = userDao.add(userExistingSalt);
        //then
        assertFalse(actualIsAdded);
        List<User> actualUsers = findAllUsers();
        assertEquals(2, actualUsers.size());
        assertFalse(actualUsers.contains(userExistingSalt));
    }

    @Test
    void addNullSalt() throws SQLException {
        //prepare
        User userNullSalt = getUserByNumber(3);
        userNullSalt.setId(3);
        userNullSalt.setSalt(null);
        //when
        boolean actualIsAdded = userDao.add(userNullSalt);
        //then
        assertFalse(actualIsAdded);
        List<User> actualUsers = findAllUsers();
        assertEquals(2, actualUsers.size());
        assertFalse(actualUsers.contains(userNullSalt));
    }

    @Test
    void addExistingHash() throws SQLException {
        //prepare
        User userExistingHash = getUserByNumber(3);
        userExistingHash.setId(3);
        userExistingHash.setHash("hash2");
        //when
        boolean actualIsAdded = userDao.add(userExistingHash);
        //then
        assertFalse(actualIsAdded);
        List<User> actualUsers = findAllUsers();
        assertEquals(2, actualUsers.size());
        assertFalse(actualUsers.contains(userExistingHash));
    }

    @Test
    void addNullHash() throws SQLException {
        //prepare
        User userNullHash = getUserByNumber(3);
        userNullHash.setId(3);
        userNullHash.setHash(null);
        //when
        boolean actualIsAdded = userDao.add(userNullHash);
        //then
        assertFalse(actualIsAdded);
        List<User> actualUsers = findAllUsers();
        assertEquals(2, actualUsers.size());
        assertFalse(actualUsers.contains(userNullHash));
    }

    @Test
    void findById() {
        //prepare
        long id = 1;
        User expectedUser = getUserByNumber(1);
        expectedUser.setId(id);
        //when
        Optional<User> actualOptionalUser = userDao.findById(id);
        //then
        assertTrue(actualOptionalUser.isPresent());
        assertEquals(expectedUser, actualOptionalUser.get());
    }

    @Test
    void findByNonExistentId() {
        //prepare
        long id = 3;
        //when
        Optional<User> actualOptionalUser = userDao.findById(id);
        //then
        assertTrue(actualOptionalUser.isEmpty());
    }

    @Test
    void findByEmail() {
        //prepare
        User expectedUser = getUserByNumber(1);
        expectedUser.setId(1);
        String existingEmail = expectedUser.getEmail();
        //when
        Optional<User> actualOptionalUser = userDao.findByEmail(existingEmail);
        //then
        assertTrue(actualOptionalUser.isPresent());
        assertEquals(expectedUser, actualOptionalUser.get());
    }

    @Test
    void findByNonExistentEmail() {
        //prepare
        String nonExistentEmail = "user3@email.com";
        //when
        Optional<User> actualOptionalUser = userDao.findByEmail(nonExistentEmail);
        //then
        assertTrue(actualOptionalUser.isEmpty());
    }

}