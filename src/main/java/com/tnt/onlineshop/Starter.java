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
import com.tnt.onlineshop.web.servlets.SignOutServlet;
import com.tnt.onlineshop.web.servlets.SignUpServlet;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.Timer;
import java.util.TimerTask;

public class Starter {

    public static final PropertiesReader PROPERTIES_READER = new PropertiesReader();

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

        //UTIL, deleting too old expired
        TimerTask deleteTooOldExpiredSessionsTask = new TimerTask() {
            @Override
            public void run() {
                sessionService.deleteTooOldExpired();
            }
        };
        Timer updateTimer = new Timer("Update Timer");
        updateTimer.scheduleAtFixedRate(deleteTooOldExpiredSessionsTask, 0, 1000 * 60 * 2);

        //WEB
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        ProductServlet productServlet = new ProductServlet(productService, sessionService);
        SignInServlet signInServlet = new SignInServlet(userService, sessionService);
        SignUpServlet signUpServlet = new SignUpServlet(userService);
        SignOutServlet signOutServlet = new SignOutServlet(sessionService);

        servletContextHandler.addServlet(new ServletHolder(productServlet), "/products/*");
        servletContextHandler.addServlet(new ServletHolder(signInServlet), "/sign-in");
        servletContextHandler.addServlet(new ServletHolder(signUpServlet), "/sign-up");
        servletContextHandler.addServlet(new ServletHolder(signOutServlet), "/sign-out");

        Server server = new Server(Integer.parseInt(propertiesReader.getProperty("appPort")));
        server.setHandler(servletContextHandler);
        server.start();
    }

}
