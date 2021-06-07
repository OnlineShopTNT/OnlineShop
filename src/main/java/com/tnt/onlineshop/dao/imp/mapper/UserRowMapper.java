package com.tnt.onlineshop.dao.imp.mapper;

import com.tnt.onlineshop.dao.RowMapper;
import com.tnt.onlineshop.entity.User;

import java.sql.ResultSet;

public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet) {
        User user = new User();
        try {
            user.setId(resultSet.getLong("id"));
            user.setEmail(resultSet.getString("email"));
            user.setHash(resultSet.getString("hash"));
            user.setSalt(resultSet.getString("salt"));
            user.setIterations(resultSet.getInt("iterations"));
            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
