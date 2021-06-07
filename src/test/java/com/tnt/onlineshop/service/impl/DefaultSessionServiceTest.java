package com.tnt.onlineshop.service.impl;

import com.tnt.onlineshop.entity.Session;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.service.SessionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultSessionServiceTest {

    private final SessionService sessionService = new DefaultSessionService();
    private User user;

    @BeforeAll
    void beforeAll(){
        user = new User();
        user.setId(1L);
        user.setHash("password");
        user.setEmail("test@online-shop.com");
        user.setIterations(10000);
        user.setSalt("salt");
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
    }

}