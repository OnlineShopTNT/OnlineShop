package com.tnt.onlineshop.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesReaderTest {

    @Test
    @DisplayName("Checks properties read from the right file when the project is running locally.")
    void testPropertiesReaderAsOnLocalMachine() throws Exception {
        final PropertiesReader[] propertiesReader = new PropertiesReader[1];
        withEnvironmentVariable("env", null)
                .execute(() -> propertiesReader[0] = new PropertiesReader());

        assertEquals("org.postgresql.Driver", propertiesReader[0].getProperty("jdbc.driver"));
        assertEquals("2", propertiesReader[0].getProperty("jdbc.maximum.pool.size"));
        assertEquals("jwnvupzaubzrkp", propertiesReader[0].getProperty("jdbc.user"));
        assertEquals("7bb7a80bd6f63d9f8ae63b3ca11caa8447fb899d1d1e060c5d9360ad4f8bda74",
                propertiesReader[0].getProperty("jdbc.password"));
        assertEquals("jdbc:postgresql://ec2-54-220-35-19.eu-west-1.compute.amazonaws.com:5432/dds8eieb7h8c9u",
                propertiesReader[0].getProperty("jdbc.url"));
    }

    @Test
    @DisplayName("Checks properties read from the right file when the project is running on Heroku.")
    void testPropertiesReaderAsOnHeroku() throws Exception {
        final PropertiesReader[] propertiesReader = new PropertiesReader[1];
        withEnvironmentVariable("env", "PROD")
                .and("DATABASE_URL", "postgres://allconsonantsuser:longpassword" +
                        "@some-amazonw:5122/lostamongotherdatabases")
                .and("PORT", "4125")
                .execute(() -> propertiesReader[0] = new PropertiesReader());
        assertEquals("allconsonantsuser", propertiesReader[0].getProperty("jdbc.user"));
        assertEquals("longpassword", propertiesReader[0].getProperty("jdbc.password"));
        assertEquals("jdbc:postgresql://some-amazonw:5122/lostamongotherdatabases",
                propertiesReader[0].getProperty("jdbc.url"));
        assertEquals("org.postgresql.Driver", propertiesReader[0].getProperty("jdbc.driver"));
        assertEquals("2", propertiesReader[0].getProperty("jdbc.maximum.pool.size"));
        assertEquals("4125", propertiesReader[0].getProperty("appPort"));
    }

}