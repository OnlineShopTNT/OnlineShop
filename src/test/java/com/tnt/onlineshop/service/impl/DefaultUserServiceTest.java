package com.tnt.onlineshop.service.impl;

import com.tnt.onlineshop.dao.UserDao;
import com.tnt.onlineshop.entity.User;
import com.tnt.onlineshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultUserServiceTest {

    private final UserDao mockedUserDao = mock(UserDao.class);
    private final UserService userService = new DefaultUserService(mockedUserDao);

    @BeforeEach
    void beforeEach() {
        reset(mockedUserDao);
    }

    @Test
    void findByEmail() {
        //prepare
        String email = "email@tnt.ua";
        Optional<User> expectedOptionalUser = Optional.of(new User());
        when(mockedUserDao.findByEmail(email)).thenReturn(expectedOptionalUser);
        //when
        Optional<User> actualOptionalUser = userService.findByEmail(email);
        //then
        assertSame(expectedOptionalUser, actualOptionalUser);
        verify(mockedUserDao).findByEmail(email);
    }

    @Test
    void findByNotNullEmail() {
        //when
        assertThrows(IllegalArgumentException.class, () -> userService.findByEmail(null));
        //then
        verify(mockedUserDao, never()).findByEmail(null);
    }

    @Test
    void findById() {
        //prepare
        long id = 1;
        Optional<User> expectedOptionalUser = Optional.of(new User());
        expectedOptionalUser.get().setId(id);
        when(mockedUserDao.findById(id)).thenReturn(expectedOptionalUser);
        //when
        Optional<User> actualOptionalUser = userService.findById(id);
        //then
        assertSame(expectedOptionalUser, actualOptionalUser);
        verify(mockedUserDao).findById(id);
    }

    @Test
    void findByInvalidId() {
        //prepare
        long id = -1;
        //when
        assertThrows(IllegalArgumentException.class, () -> userService.findById(id));
        //then
        verify(mockedUserDao, never()).findById(id);
    }

}