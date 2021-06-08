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

    private List<Session> sessions = new ArrayList<>();

    @Override
    public Optional<Session> getByToken(String token) {
        Optional<Session> optionalSession = sessions.stream()
                .filter(session -> session.getToken().equals(token))
                .findFirst();
        if (optionalSession.isPresent()){
            Session userSession = optionalSession.get();
            if (userSession.getExpireDate().compareTo(LocalDateTime.now()) < 0){
                sessions.remove(userSession);
                return Optional.empty();
            } else {
                return optionalSession;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Session> getByUser(User user) {
        Optional<Session> optionalSession = Optional.empty();
        for (Session session : sessions) {
            if (session.getUser().equals(user)) {
                if (session.getExpireDate().compareTo(LocalDateTime.now()) < 0) {
                    session.setToken(UUID.randomUUID().toString());
                    session.setExpireDate(LocalDateTime.now().plusHours(5));
                }
                optionalSession = Optional.of(session);
            }
        }
        if (optionalSession.isEmpty()) {
            optionalSession = add(user);
        }
        return optionalSession;
    }

    @Override
    public boolean delete(String token) {
        boolean isDeleted = false;
        int index = sessions.stream()
                .filter(session -> session.getToken().equals(token))
                .map(session -> sessions.indexOf(session))
                .findFirst()
                .orElse(-1);
        if (index >= 0) {
            sessions.remove(index);
            isDeleted = true;
        }
        return isDeleted;
    }

    Optional<Session> add(User user) {
        Session session = new Session();
        session.setToken(UUID.randomUUID().toString());
        session.setUser(user);
        session.setExpireDate(LocalDateTime.now().plusHours(5));
        sessions.add(session);
        return Optional.of(session);
    }

    List<Session> getSessions() {
        return sessions;
    }

}
