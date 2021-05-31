package com.tnt.onlineshop.dao.imp;

import com.tnt.onlineshop.dao.UserDao;
import com.tnt.onlineshop.dao.imp.mapper.UserRowMapper;
import com.tnt.onlineshop.entity.User;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcUserDao implements UserDao {

    private final UserRowMapper userRowMapper = new UserRowMapper();
    private final static String FIND_BY_EMAIL = "SELECT id, email, hash, salt, iterations" +
            " FROM public.users" +
            " WHERE email=?";
    private final static String FIND_BY_ID = "SELECT id, email, hash, salt, iterations" +
            " FROM public.users" +
            " WHERE id=?";
    private static final String INSERT = "INSERT INTO public.users" +
            " (email, password, salt, iterations)" +
            " VALUES (?, ?, ?, ?)";

    private final HikariDataSource dataSource;

    public JdbcUserDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean add(User user) {
        boolean isAdded = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
            insertStatement.setString(1, user.getEmail());
            insertStatement.setString(2, user.getHash());
            insertStatement.setString(3, user.getSalt());
            insertStatement.setInt(4, user.getIterations());
            int result = insertStatement.executeUpdate();
            if (result > 0) {
                isAdded = true;
            }
        } catch (PSQLException e) {
            e.printStackTrace();
            isAdded = false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return isAdded;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByEmailStatement = connection.prepareStatement(FIND_BY_EMAIL)) {
            findByEmailStatement.setString(1, email);
            try (ResultSet resultSet = findByEmailStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = userRowMapper.mapRow(resultSet);
                    if (resultSet.next()) {
                        throw new RuntimeException("More than one user was found for id");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findById(long id) {
        User user = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByEmailStatement = connection.prepareStatement(FIND_BY_ID)) {
            findByEmailStatement.setLong(1, id);
            try (ResultSet resultSet = findByEmailStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = userRowMapper.mapRow(resultSet);
                    if (resultSet.next()) {
                        throw new RuntimeException("More than one user was found for id");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(user);
    }

}
