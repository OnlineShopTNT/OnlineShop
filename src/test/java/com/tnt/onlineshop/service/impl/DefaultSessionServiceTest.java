package com.tnt.onlineshop.service.impl;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.entity.User;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultSessionServiceTest {

    private final DefaultSessionService sessionService = new DefaultSessionService();
    private User user;
    private User user2;
    private List<Session> sessions = sessionService.getSessions();
    private Session session;

    @BeforeAll
    void beforeAll() {
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

    @BeforeEach
    void beforeEach() {
        session = new Session();
        session.setToken("some-token");
        session.setUser(user);
        session.setExpireDate(LocalDateTime.now().plusHours(5));
        sessions.add(session);
    }

    @AfterEach
    void afterEach() {
        sessions.remove(session);
    }

    @Test
    @DisplayName("Add session")
    void addTest() {
        //when
        Optional<Session> optionalSession = sessionService.add(user2);
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user2.getId(), optionalSession.get().getUser().getId());
        assertTrue(sessions.contains(optionalSession.get()));
        sessions.remove(optionalSession.get());
        assertFalse(sessions.contains(optionalSession.get()));
    }

    @Test
    @DisplayName("Get session by token")
    void getByTokenTest() {
        //when
        Optional<Session> optionalSession = sessionService.getByToken("some-token");
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user.getId(), optionalSession.get().getUser().getId());
    }

    @Test
    @DisplayName("Get session by user if the session list is empty")
    void getByUserIfSessionListIsEmptyTest() {
        //prepared
        List<Session> sessionList = new ArrayList<>(sessions);
        sessions.clear();
        //when
        Optional<Session> optionalSession = sessionService.getByUser(user);
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user.getId(), optionalSession.get().getUser().getId());
        sessions = new ArrayList<>(sessionList);
    }

    @Test
    @DisplayName("Get session by user if the session doesn't exists")
    void getByUserIfSessionDoesNotExistTest() {
        //when
        Optional<Session> optionalSession = sessionService.getByUser(user2);
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user2.getId(), optionalSession.get().getUser().getId());
        assertEquals(LocalDateTime.now().plusHours(5).getHour(), optionalSession.get().getExpireDate().getHour());
        assertTrue(sessions.remove(optionalSession.get()));
    }

    @Test
    @DisplayName("Get session by user if the session exists, the token is not active")
    void getByUserIfSessionExistAndTokenIsNotActiveTest() {
        //prepare
        Session session1 = new Session();
        session1.setToken("second-token");
        session1.setUser(user2);
        session1.setExpireDate(LocalDateTime.now().minusDays(1));
        sessions.add(session1);
        //when
        Optional<Session> optionalSession = sessionService.getByUser(user2);
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user2.getId(), optionalSession.get().getUser().getId());
        assertEquals(LocalDateTime.now().plusHours(5).getHour(), optionalSession.get().getExpireDate().getHour());
        sessions.remove(session1);
    }

    @Test
    @DisplayName("Get session by user if the session exists, the token is active")
    void getByUserIfSessionExistAndTokenIsActiveTest() {
        //when
        Optional<Session> optionalSession = sessionService.getByUser(user);
        //then
        assertTrue(optionalSession.isPresent());
        assertEquals(user.getId(), optionalSession.get().getUser().getId());
        assertEquals(session.getExpireDate().getHour(), optionalSession.get().getExpireDate().getHour());
    }

    @Test
    @DisplayName("Delete session if session exist")
    void deleteExistedSessionTest(){
        //prepare
        Session session1 = new Session();
        session1.setToken("second-token");
        session1.setUser(user2);
        session1.setExpireDate(LocalDateTime.now().plusHours(5));
        sessions.add(session1);
        //when
        boolean isDeleted = sessionService.delete("second-token");
        //then
        assertTrue(isDeleted);
    }

    @Test
    @DisplayName("Delete session")
    void deleteSessionWhenSessionDoesNotExistTest(){
        //when
        boolean isDeleted = sessionService.delete("second-token");
        //then
        assertFalse(isDeleted);
    }

}