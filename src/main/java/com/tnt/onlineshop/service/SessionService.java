package com.tnt.onlineshop.service;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.entity.User;

import java.util.Optional;

public interface SessionService {

    Optional<Session> add(User user);

    Optional<Session> getByToken(String token);

    Optional<Session> getByUser(User user);

    boolean delete(String token);
}
