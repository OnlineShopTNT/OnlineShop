package com.tnt.onlineshop.dao.imp.mapper;

import com.tnt.onlineshop.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductRowMapperTest {

    private final ProductRowMapper rowMapper = new ProductRowMapper();

    @Test
    @DisplayName("Checks if product created from resultSet has all fields right.")
    void mapRowTest() throws SQLException {
    //prepare
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("productname");
        when(resultSet.getDouble("price")).thenReturn(2.0);
        when(resultSet.getTimestamp("last_modified_time")).thenReturn(new Timestamp(1000));
        //when
        Product product = rowMapper.mapRow(resultSet);
        //then
        assertEquals(1, product.getId());
        assertEquals("productname", product.getName());
        assertEquals(2., product.getPrice());
        assertEquals(new Timestamp(1000), product.getLastModifiedTime());
    }

}