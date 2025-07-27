package com.automation.demo.ui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.Logger; // Import Log4j2 Logger

public class ConfigReader {

    private static Properties properties;
    private static final Logger logger = LoggerUtil.getLogger(ConfigReader.class);

    // Static block to load properties when the class is first loaded
    static {
        properties = new Properties();
        String configFilePath = "config.properties"; // This is the file name.
        // It needs to be on the classpath.
        // Maven automatically puts src/test/resources on the classpath.
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(configFilePath)) {
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Successfully loaded configuration from: " + configFilePath);
            } else {
                // This is critical if the file is not found
                logger.error("Configuration file '" + configFilePath + "' not found in the classpath.");
                throw new RuntimeException("Configuration file '" + configFilePath + "' not found.");
            }
        } catch (IOException e) {
            logger.error("Error loading configuration file '" + configFilePath + "': " + e.getMessage(), e);
            throw new RuntimeException("Failed to load configuration file: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a property value as a String from the loaded configuration.
     *
     * @param key The key of the property.
     * @return The String value of the property, or null if the key is not found.
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property '" + key + "' not found in config.properties.");
        }
        return value;
    }

    /**
     * Retrieves a property value as an int from the loaded configuration.
     * This method assumes the property must exist and be a valid integer.
     *
     * @param key The key of the property (e.g., "default.implicit.wait.seconds").
     * @return The int value of the property.
     * @throws IllegalArgumentException if the property is not found or is empty.
     * @throws NumberFormatException if the property value cannot be parsed as an integer.
     */
    public static int getIntProperty(String key) {
        String value = getProperty(key); // Use existing getProperty to fetch string value first
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Property '" + key + "' not found or is empty in config.properties. Cannot convert to int.");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.error("Property '" + key + "' with value '" + value + "' is not a valid integer.", e);
            throw new NumberFormatException("Property '" + key + "' value '" + value + "' is not a valid integer.");
        }
    }

}