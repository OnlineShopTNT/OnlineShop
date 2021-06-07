package com.tnt.onlineshop.service.impl;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.service.SessionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultSessionServiceTest {

    private final SessionService sessionService = new DefaultSessionService();
    private User user;
    private User user2;

    @BeforeAll
    void beforeAll(){
        user = new User();
        user.setId(1L);
        user.setHash("password");
        user.setEmail("test@online-shop.com");
        user.setIterations(10000);
        user.setSalt("salt");

        user2 = new User();
        user2.setId(2L);
        user2.setHash("password2");
        user2.setEmail("test2@online-shop.com");
        user2.setIterations(10000);
        user2.setSalt("salt2");
    }

    @Test
    @DisplayName("Add session")
    void addTest(){
        //when
        Optional<Session> optionalSession = sessionService.add(user);
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user.getId(), optionalSession.get().getUser().getId());
        assertEquals(1, optionalSession.get().getId());
        assertTrue(sessionService.delete(optionalSession.get().getToken()));
    }

    @Test
    @DisplayName("Get session by token")
    void getByTokenTest(){
        //prepared
        Optional<Session> session = sessionService.add(user);
        //when
        Optional<Session> optionalSession = sessionService.getByToken(session.get().getToken());
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user.getId(), optionalSession.get().getUser().getId());
        assertEquals(1, optionalSession.get().getId());
        assertTrue(sessionService.delete(optionalSession.get().getToken()));
    }

    @Test
    @DisplayName("Get session by user if the session exists, the token is not active")
    void getByUserIfSessionExistAndTokenIsNotActiveTest(){
        //prepared
        Optional<Session> session = sessionService.add(user);
        session.get().setExpireDate(LocalDateTime.now().minusDays(1));
        //when
        Optional<Session> optionalSession = sessionService.getByUser(session.get().getUser());
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user.getId(), optionalSession.get().getUser().getId());
        assertEquals(LocalDateTime.now().plusHours(5).getHour(), session.get().getExpireDate().getHour());
        assertEquals(1, optionalSession.get().getId());
        assertTrue(sessionService.delete(optionalSession.get().getToken()));
    }

    @Test
    @DisplayName("Get session by user if the session exists, the token is active")
    void getByUserIfSessionExistAndTokenIsActiveTest(){
        //prepared
        Optional<Session> session = sessionService.add(user);
        //when
        Optional<Session> optionalSession = sessionService.getByUser(session.get().getUser());
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user.getId(), optionalSession.get().getUser().getId());
        assertEquals(session.get().getExpireDate(), optionalSession.get().getExpireDate());
        assertEquals(1, optionalSession.get().getId());
        assertTrue(sessionService.delete(optionalSession.get().getToken()));
    }

    @Test
    @DisplayName("Get session by user if the session doesn't exists")
    void getByUserIfSessionDoesNotExistTest(){
        //prepare
        sessionService.add(user2);
        //when
        Optional<Session> optionalSession = sessionService.getByUser(user);
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user.getId(), optionalSession.get().getUser().getId());
        assertEquals(1, optionalSession.get().getId());
        assertTrue(sessionService.delete(optionalSession.get().getToken()));
    }

}