
package com.automation.demo.tests;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import com.automation.demo.utils.ConfigReader;
import com.automation.demo.utils.DriverFactory;
import com.automation.demo.utils.DriverManager;
import com.automation.demo.utils.LoggerUtil; // Import your LoggerUtil
import org.apache.logging.log4j.Logger; // Import Log4j2 Logger
import com.automation.demo.utils.ScreenshotUtil;

public class BaseTest {

    private static final Logger logger = LoggerUtil.getLogger(BaseTest.class);

    @BeforeMethod
    @Parameters("browserName")
    public void setUp(@Optional("") String browserName) {
        String browserToUse;

        // 1. Prioritize browser name from TestNG XML / Maven command line parameter
        if (browserName != null && !browserName.trim().isEmpty()) {
            browserToUse = browserName;
            logger.debug("Browser selected from TestNG XML/Maven parameter: " + browserToUse);
        } else {
            // 2. Fallback to default browser from config.properties
            String browserFromConfig = ConfigReader.getProperty("default.browser");
            if (browserFromConfig != null && !browserFromConfig.trim().isEmpty()) {
                browserToUse = browserFromConfig;
                logger.debug("Browser selected from config.properties: " + browserToUse);
            } else {
                // 3. Ultimate hardcoded fallback
                browserToUse = "chrome";
                logger.warn("No browser specified via parameter or config.properties. Defaulting to Chrome.");
            }
        }

        WebDriver webDriver = DriverFactory.createDriver(browserToUse);
        DriverManager.setDriver(webDriver);
        DriverManager.getDriver();
        logger.info("Webdriver initialized for browser: " + browserName);
        System.out.println("Browser opened in thread: " + Thread.currentThread().threadId());
    }

    @AfterMethod
    public void tearDown(ITestResult result) { // Added ITestResult parameter
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            //Take screenshot on failure
            if (result.getStatus() == ITestResult.FAILURE) {
                logger.error("Test failed: " + result.getName() + ".FAILED! Taking screenshot.");
                ScreenshotUtil.takeScreenshot(driver, result.getName());
            } else {
                logger.info("Test passed: " + result.getName());
            }
            logger.info("Closing the browser");
            driver.quit();
            DriverManager.unload();
            logger.info("Browser closed and driver unloaded");
        }
        System.out.println("Teardown finished in thread: " + Thread.currentThread().threadId());
    }
}

