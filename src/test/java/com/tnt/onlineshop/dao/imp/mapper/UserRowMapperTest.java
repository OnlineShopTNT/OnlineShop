package com.tnt.onlineshop.dao.imp.mapper;

import com.tnt.onlineshop.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRowMapperTest {

    @Test
    @DisplayName("Checks if user created from resultSet")
    void mapRowTest() throws SQLException {
        //prepare
        UserRowMapper rowMapper = new UserRowMapper();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("email")).thenReturn("test@test");
        when(resultSet.getString("hash")).thenReturn("hashedPassword");
        when(resultSet.getString("salt")).thenReturn("qwerty12345");
        when(resultSet.getInt("iterations")).thenReturn(10000);
        //when
        User user = rowMapper.mapRow(resultSet);
        //then
        assertEquals(1L, user.getId());
        assertEquals("test@test", user.getEmail());
        assertEquals("hashedPassword", user.getHash());
        assertEquals("qwerty12345", user.getSalt());
        assertEquals(10000, user.getIterations());
    }


}