package com.tnt.onlineshop.service.impl;

import com.tnt.onlineshop.dao.UserDao;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.security.SecurityService;
import com.tnt.onlineshop.service.UserService;

import java.util.Optional;

public class DefaultUserService implements UserService {

    private final UserDao userDao;
    private final SecurityService securityService;

    public DefaultUserService(UserDao userDao, SecurityService securityService) {
        this.userDao = userDao;
        this.securityService = securityService;
    }

    @Override
    public boolean add(String email, String password) {
        boolean isAdded = false;
        if (email != null && password != null) {
            User userToAdd = securityService
                    .createUserWithHashedPassword(email, password.toCharArray());
            isAdded = userDao.add(userToAdd);
        }
        return isAdded;
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

    @Override
    public Optional<User> signInCheck(String email, String password) {
        Optional<User> optionalUser = Optional.empty();
        if (email != null && password != null) {
            Optional<User> optionalUserFromDataBase = userDao.findByEmail(email);
            if (optionalUserFromDataBase.isPresent()) {
                if (securityService.checkPasswordAgainstUserPassword(
                        optionalUserFromDataBase.get(), password.toCharArray())) {
                    optionalUser = optionalUserFromDataBase;
                }
            }
        }
        return optionalUser;
    }

}
