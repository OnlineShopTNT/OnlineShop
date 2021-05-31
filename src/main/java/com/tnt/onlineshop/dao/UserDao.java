package com.tnt.onlineshop.dao;

import com.tnt.onlineshop.entity.User;

import java.util.Optional;

public interface UserDao {

    boolean add(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(long id);

}
