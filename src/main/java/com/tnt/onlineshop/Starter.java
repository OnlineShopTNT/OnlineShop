package com.tnt.onlineshop;

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
import com.tnt.onlineshop.util.PropertiesReader;
import com.tnt.onlineshop.web.servlets.ProductServlet;
import com.tnt.onlineshop.web.servlets.SignInServlet;
import com.tnt.onlineshop.web.servlets.SignUpServlet;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.sql.DataSource;

public class Starter {

    public static void main(String[] args) throws Exception {

        //DAO
        HikariDataSource dataSource;
        HikariConfig config = new HikariConfig();
        PropertiesReader propertiesReader = new PropertiesReader();
        config.setJdbcUrl(propertiesReader.getProperty("jdbc.url"));
        config.setUsername(propertiesReader.getProperty("jdbc.user"));
        config.setPassword(propertiesReader.getProperty("jdbc.password"));
        config.setDriverClassName(propertiesReader.getProperty("jdbc.driver"));
        config.setMaximumPoolSize(Integer.parseInt(propertiesReader.getProperty("jdbc.maximum.pool.size")));
        dataSource = new HikariDataSource(config);

        ProductDao productDao = new JdbcProductDao(dataSource);
        UserDao userDao = new JdbcUserDao(dataSource);

        //SERVICE
        SecurityService securityService = new DefaultSecurityService();
        ProductService productService = new DefaultProductService(productDao);
        UserService userService = new DefaultUserService(userDao, securityService);
        SessionService sessionService = new DefaultSessionService();

        //WEB
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        ProductServlet productServlet = new ProductServlet(productService);
        SignInServlet signInServlet = new SignInServlet(userService, sessionService);
        SignUpServlet signUpServlet = new SignUpServlet(userService);

        servletContextHandler.addServlet(new ServletHolder(productServlet), "/products/*");
        servletContextHandler.addServlet(new ServletHolder(signInServlet), "/sign-in");
        servletContextHandler.addServlet(new ServletHolder(signUpServlet), "/sign-up");

        Server server = new Server(Integer.parseInt(propertiesReader.getProperty("appPort")));
        server.setHandler(servletContextHandler);
        server.start();
    }

}
