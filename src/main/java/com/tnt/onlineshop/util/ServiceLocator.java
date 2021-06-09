package com.tnt.onlineshop.util;

import com.tnt.onlineshop.dao.ProductDao;
import com.tnt.onlineshop.dao.UserDao;
import com.tnt.onlineshop.dao.imp.JdbcProductDao;
import com.tnt.onlineshop.dao.imp.JdbcUserDao;
import com.tnt.onlineshop.security.SecurityService;
import com.tnt.onlineshop.security.impl.DefaultSecurityService;
import com.tnt.onlineshop.service.ProductService;
import com.tnt.onlineshop.service.SessionService;
import com.tnt.onlineshop.service.UserService;
import com.tnt.onlineshop.service.impl.DefaultProductService;
import com.tnt.onlineshop.service.impl.DefaultSessionService;
import com.tnt.onlineshop.service.impl.DefaultUserService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {

    private static final Map<String, Object> SERVICE_MAP = new HashMap<>();
    public static final PropertiesReader PROPERTIES_READER = new PropertiesReader();

    static {
        //DAO
        PropertiesReader propertiesReader = new PropertiesReader();
        HikariDataSource dataSource;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(propertiesReader.getProperty("jdbc.url"));
        config.setUsername(propertiesReader.getProperty("jdbc.user"));
        config.setPassword(propertiesReader.getProperty("jdbc.password"));
        config.setDriverClassName(propertiesReader.getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getProperty("jdbc.maximum.pool.size")));
        dataSource = new HikariDataSource(config);

        UserDao userDao = new JdbcUserDao(dataSource);
        ProductDao productDao = new JdbcProductDao(dataSource);

        //SERVICE
        SecurityService securityService = new DefaultSecurityService();
        ProductService productService = new DefaultProductService(productDao);
        SessionService sessionService = new DefaultSessionService();
        UserService userService = new DefaultUserService(userDao, securityService);

        SERVICE_MAP.put("productService", productService);
        SERVICE_MAP.put("sessionService", sessionService);
        SERVICE_MAP.put("userService", userService);
    }

    public static Object gerServiceMap(String service){
        return SERVICE_MAP.get(service);
    }

}
