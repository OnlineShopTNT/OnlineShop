package com.tnt.onlineshop.dao;

import com.tnt.onlineshop.entity.User;

import java.util.Optional;

public interface UserDao {

    boolean add();

    Optional<User> findByEmail();

    Optional<User> findById();

}
