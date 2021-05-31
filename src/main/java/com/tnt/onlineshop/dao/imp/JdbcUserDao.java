package com.tnt.onlineshop.dao.imp;

import com.tnt.onlineshop.dao.UserDao;
import com.tnt.onlineshop.entity.User;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Optional;

public class JdbcUserDao implements UserDao {

    private final HikariDataSource dataSource;

    public JdbcUserDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean add(User user) {
        return false;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.empty();
    }

}
