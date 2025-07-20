package com.automation.demo.tests;



import org.openqa.selenium.WebDriver;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import com.automation.demo.utils.DriverFactory;
import com.automation.demo.utils.DriverManager;

import com.automation.demo.utils.LoggerUtil; // Import your LoggerUtil


import org.apache.logging.log4j.Logger; // Import Log4j2 Logger



public class BaseTest {
    protected WebDriver driver;
    private static final Logger logger = LoggerUtil.getLogger(BaseTest.class);

    @BeforeMethod
    @Parameters("browserName")
    public void setUp(String browserName) {
        WebDriver webDriver = DriverFactory.createDriver(browserName);
        DriverManager.setDriver(webDriver);
        driver = DriverManager.getDriver();
        logger.info("Webdriver initialized for browser: " + browserName);
        System.out.println("Browser opened in thread: " + Thread.currentThread().threadId());
       
    }

    @AfterMethod
    public void tearDown() {
       if (DriverManager.getDriver() !=null) {
            logger.info("Closing the browser");
            DriverManager.getDriver().quit();
            DriverManager.unload();
            logger.info("Browser closed and driver unloaded");
        }
        System.out.println("Teardown finished in thread: " + Thread.currentThread().threadId());
        }
        
    }

