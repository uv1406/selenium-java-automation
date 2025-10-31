package com.automation.demo.ui.utils; // Or your actual package

import org.openqa.selenium.WebDriver;
// Assuming LoggerUtil exists

 import org.apache.logging.log4j.Logger;


public class DriverManager {

     private static final Logger logger = LoggerUtil.getLogger(DriverManager.class); // Optional logging
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            try {
                //Call to factory to create the driver
                driver = DriverFactory.createDriver();
                driverThreadLocal.set(driver);
                logger.info("WebDriver instance created and set for Thread {}", Thread.currentThread().threadId()); // Optional log
            } catch (Exception e) {
                // TODO: handle exception

                logger.fatal("Error creating WebDriver instance for Thread {}", Thread.currentThread().threadId(), e); // Optional log
                throw new RuntimeException("Failed to create WebDriver instance", e);
            }

        }
        return driver;
    }

    public static void setDriver(WebDriver driverInstance) {
        if (driverInstance != null) {
            driverThreadLocal.set(driverInstance);
             logger.info("WebDriver instance set for Thread {}", Thread.currentThread().threadId()); // Optional log
        } else {
              logger.warn("Attempted to set a null WebDriver instance for Thread {}", Thread.currentThread().toString()); // Optional log
        }
    }

    /**
     * Quits the WebDriver instance for the current thread and removes it from storage.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            long threadId = Thread.currentThread().threadId(); // For logging
             logger.info("Attempting to quit WebDriver for Thread {}.", threadId); // Optional log
            try {
                driver.quit(); // <<< CRITICAL: Actually close the browser and end the session
                 logger.info("WebDriver quit successfully for Thread {}.", threadId); // Optional log
            } catch (Exception e) {
                logger.error("Error occurred while quitting WebDriver for Thread {}.", threadId, e); // Optional log
            } finally {
                driverThreadLocal.remove(); // Always remove from ThreadLocal
                 logger.info("Removed WebDriver from ThreadLocal storage for Thread {}.", threadId); // Optional log
            }
        } else {
            logger.warn("Attempted to quit driver, but none found for Thread {}.", Thread.currentThread().threadId()); // Optional log
        }
    }
}