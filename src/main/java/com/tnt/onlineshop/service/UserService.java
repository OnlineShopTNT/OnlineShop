package com.tnt.onlineshop.service;

import com.tnt.onlineshop.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    Optional<User> findById(long id);

    boolean add(String email, String password);

    Optional<User> signInCheck(String email, String password);

}
