package com.automation.demo.ui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.Logger; // Import Log4j2 Logger

public class ConfigReader {

    private static final Properties properties;
    private static final Logger logger = LoggerUtil.getLogger(ConfigReader.class);

    // Static block to load properties from the file once
    static {
        properties = new Properties();
        String configFilePath = "config.properties"; // File in src/test/resources
        
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(configFilePath)) {
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Successfully loaded configuration from: {}", configFilePath);
            } else {
                logger.error("Configuration file '{}' not found in the classpath.", configFilePath);
                throw new RuntimeException("Configuration file '" + configFilePath + "' not found.");
            }
        } catch (IOException e) {
            logger.error("Error loading configuration file '{}'", configFilePath, e);
            throw new RuntimeException("Failed to load configuration file.", e);
        }
    }

    /**
     * Retrieves a property value by checking System Properties, then Environment Variables,
     * and finally the config.properties file.
     *
     * @param key The key of the property.
     * @return The String value of the property, or null if the key is not found anywhere.
     */
    public static String getProperty(String key) {
        String value = null;

        // 1. Check Java System Properties (Highest priority, e.g., mvn -Dkey=value)
        value = System.getProperty(key);
        if (value != null && !value.isEmpty()) {
            logger.debug("Fetched property '{}' from System Property: {}", key, value);
            return value;
        }

        // 2. Check Environment Variables (Second priority, e.g., CI/CD pipeline)
        value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            logger.debug("Fetched property '{}' from Environment Variable: {}", key, value);
            return value;
        }

        // 3. Fallback to config.properties file (Lowest priority)
        value = properties.getProperty(key);
        if (value != null && !value.isEmpty()) {
            logger.debug("Fetched property '{}' from config.properties file: {}", key, value);
        } else {
            logger.warn("Property '{}' is not set in System Properties, Environment Variables, or config.properties.", key);
        }

        return value;
    }

    /**
     * Retrieves a property value with a default fallback.
     *
     * @param key The key of the property.
     * @param defaultValue The value to return if the key is not found.
     * @return The String value of the property, or the defaultValue.
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    /**
     * Retrieves a property value as an int.
     *
     * @param key The key of the property.
     * @return The int value of the property.
     * @throws IllegalArgumentException if the property is not found.
     * @throws NumberFormatException if the property value is not a valid integer.
     */
    public static int getIntProperty(String key) {
        String value = getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Property '" + key + "' not found or is empty. Cannot convert to int.");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.error("Property '{}' with value '{}' is not a valid integer.", key, value, e);
            throw new NumberFormatException("Property '" + key + "' value '" + value + "' is not a valid integer.");
        }
    }

    /**
     * Retrieves a property value as a boolean.
     *
     * @param key The key of the property.
     * @return The boolean value of the property.
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }
}