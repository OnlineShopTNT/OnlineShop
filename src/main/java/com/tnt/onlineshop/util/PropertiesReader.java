package com.tnt.onlineshop.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropertiesReader {
    private static final String INDEPENDENT_PROPERTIES_FILE = "application.properties";
    private static final Properties properties = new Properties();

    public PropertiesReader() {
        try (InputStream inputStream = PropertiesReader.class.getClassLoader().getResourceAsStream(INDEPENDENT_PROPERTIES_FILE)) {
            validateInputStream(inputStream);
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error during loading properties from " + INDEPENDENT_PROPERTIES_FILE, e);
        }

        if ("PROD".equals(System.getenv("env"))) {
            URI databaseUri;
            String databaseUrlVariable = System.getenv("DATABASE_URL");
            String portVariable = System.getenv("PORT");
            try {
                databaseUri = new URI(databaseUrlVariable);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Error during initializing databaseUri with URI from value " +
                        "of environmental variable \"DATABASE_URL\"" + databaseUrlVariable, e);
            }
            properties.setProperty("jdbc.user", databaseUri.getUserInfo().split(":")[0]);
            properties.setProperty("jdbc.password", databaseUri.getUserInfo().split(":")[1]);
            properties.setProperty("jdbc.url", "jdbc:postgresql://" +
                    databaseUri.getHost() + ":" +
                    databaseUri.getPort() +
                    databaseUri.getPath());
            properties.setProperty("appPort", portVariable);
        }
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    private void validateInputStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new RuntimeException(".properties file not found, input stream is null");
        }
    }
}
