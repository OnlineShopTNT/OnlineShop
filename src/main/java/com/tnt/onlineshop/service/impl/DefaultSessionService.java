package com.tnt.onlineshop.service.impl;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.service.SessionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultSessionService implements SessionService {

    private List<Session> sessions = new ArrayList<>();

    @Override
    public Optional<Session> add(User user) {
        Session session = new Session();
        session.setId(sessions.size() + 1);
        session.setToken(UUID.randomUUID().toString());
        session.setUser(user);
        session.setExpireDate(LocalDateTime.now().plusHours(5));
        sessions.add(session);
        return Optional.of(session);
    }

    @Override
    public Optional<Session> getByToken(String token) {
        return sessions.stream().filter(session -> session.getToken().equals(token)).findFirst();
    }

    @Override
    public Optional<Session> getByUser(User user) {
        Optional<Session> optionalSession = Optional.empty();
        if (sessions.isEmpty()){
            optionalSession = add(user);
        }
        for (Session session : sessions) {
            if (session.getUser().equals(user)) {
                if (session.getExpireDate().compareTo(LocalDateTime.now()) < 0) {
                    session.setToken(UUID.randomUUID().toString());
                    session.setExpireDate(LocalDateTime.now().plusHours(5));
                }
                optionalSession = Optional.of(session);
            }
        }
        if (optionalSession.isEmpty()){
            optionalSession = add(user);
        }
        return optionalSession;
    }

    @Override
    public boolean delete(String token) {
        boolean isDeleted = false;
        List<Session> sessionList = sessions.stream().filter(session -> !session.getToken().equals(token)).collect(Collectors.toList());
        if (sessionList.size() < sessions.size()) {
            sessions = new ArrayList<>(sessionList);
            isDeleted = true;
        }
        return isDeleted;
    }

    List<Session> getSessions() {
        return sessions;
    }

    void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

}
