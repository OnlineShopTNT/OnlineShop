package com.tnt.onlineshop;

import com.tnt.onlineshop.dao.imp.JdbcProductDao;
import com.tnt.onlineshop.service.ProductService;
import com.tnt.onlineshop.service.impl.DefaultProductService;
import com.tnt.onlineshop.util.PropertiesReader;
import com.tnt.onlineshop.web.servlets.ProductServlet;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

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

        JdbcProductDao jdbcProductDao = new JdbcProductDao(dataSource);

        //SERVICE
        ProductService productService = new DefaultProductService(jdbcProductDao);

        //WEB
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        ProductServlet productServlet = new ProductServlet(productService);

        servletContextHandler.addServlet(new ServletHolder(productServlet), "/products/*");

        Server server = new Server(Integer.parseInt(propertiesReader.getProperty("appPort")));
        server.setHandler(servletContextHandler);
        server.start();
    }

}
