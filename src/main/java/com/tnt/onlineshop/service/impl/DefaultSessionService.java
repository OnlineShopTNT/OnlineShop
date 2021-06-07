package com.tnt.onlineshop.service.impl;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.service.SessionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DefaultSessionService implements SessionService {

    List<Session> sessions = new ArrayList<>();

    @Override
    public Optional<Session> add(User user) {
        return Optional.empty();
    }

    @Override
    public Optional<Session> getByToken(String token) {
        return Optional.empty();
    }

    @Override
    public Optional<Session> getByUser(User user) {
        Optional<Session> optionalSession = Optional.empty();
        for (Session session : sessions) {
            if (session.getUser().equals(user)) {
                session.setToken(UUID.randomUUID().toString());
                session.setExpireDate(LocalDateTime.now().plusHours(5));
                optionalSession = Optional.of(session);
            } else {
                optionalSession = add(user);
            }
        }
        return optionalSession;
    }

    @Override
    public boolean delete(String token) {
        return false;
    }
}
