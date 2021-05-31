package com.tnt.onlineshop.service.impl;

import com.tnt.onlineshop.dao.UserDao;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.service.UserService;

import java.util.Optional;

public class DefaultUserService implements UserService {

    private final UserDao userDao;

    public DefaultUserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email != null) {
            return userDao.findByEmail(email);
        } else {
            throw new IllegalArgumentException("Email passed to UserService.findByEmail(..) is null");
        }
    }

    @Override
    public Optional<User> findById(long id) {
        if (id > 0) {
            return userDao.findById(id);
        } else {
            throw new IllegalArgumentException("Id passed to UserService.findById(..) is < 1, id: " + id);
        }
    }

}
