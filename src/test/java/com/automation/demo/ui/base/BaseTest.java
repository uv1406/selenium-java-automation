package com.automation.demo.ui.base;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import com.automation.demo.ui.utils.DriverManager;
import com.automation.demo.ui.utils.LoggerUtil;
import org.apache.logging.log4j.Logger;
import com.automation.demo.ui.utils.ScreenshotUtil;

public class BaseTest {

    private static final Logger logger = LoggerUtil.getLogger(BaseTest.class);

    @BeforeMethod(alwaysRun = true) // Use alwaysRun for reliability
    public void setUp() {
        // This ONE line triggers the entire driver initialization:
        // 1. Checks ThreadLocal (it's null)
        // 2. Calls DriverFactory.createDriver()
        // 3. DriverFactory reads ConfigReader (which checks mvn -D or config.properties)
        // 4. Creates local or remote driver
        // 5. Configures it
        // 6. Stores it in ThreadLocal
        try {
            DriverManager.getDriver(); // This is the only setup call you need.
            logger.info("WebDriver initialized by DriverManager for thread: {}", Thread.currentThread().threadId());
        
            // Optional: You can still navigate to a base URL here
            // String baseUrl = ConfigReader.getProperty("app.url");
            // if (baseUrl != null) {
            //     DriverManager.getDriver().get(baseUrl);
            // }

        } catch (Exception e) {
            logger.fatal("FATAL: Driver setup failed! Test will be skipped.", e);
            // Re-throw or use TestNG's SkipException to stop this test
            throw new RuntimeException("Driver setup failed, see logs for details.", e);
        }
    }

    @AfterMethod(alwaysRun = true) // Use alwaysRun to ensure cleanup
    public void tearDown(ITestResult result) {
        WebDriver driver = null;
        try {
            // Get the driver instance *before* trying to quit, for screenshot logic
            driver = DriverManager.getDriver(); // Get the driver for this thread
        } catch (IllegalStateException e) {
            // This can happen if setup failed so badly that getDriver() throws an error
            logger.warn("Driver was not initialized, cannot take screenshot or quit. {}", e.getMessage());
            // We still want to call quitDriver() to ensure ThreadLocal is cleaned up
        }

        // Screenshot logic
        if (driver != null && result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test failed: {}. Taking screenshot.", result.getName());
            ScreenshotUtil.takeScreenshot(driver, result.getName());
        } else if (driver != null) {
            logger.info("Test passed: {}", result.getName());
        }

        // Centralized cleanup
        // This single method handles both driver.quit() AND driver.remove()
        logger.info("Closing browser and cleaning up driver for thread: {}", Thread.currentThread().threadId());
        DriverManager.quitDriver();
    }
}