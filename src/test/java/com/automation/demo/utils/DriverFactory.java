package com.automation.demo.utils;


import org.apache.logging.log4j.Logger; // Import Log4j2 Logger
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeOptions; // For browser options

public class DriverFactory {
    private static final Logger logger= LoggerUtil.getLogger(DriverFactory.class);

    public static WebDriver createDriver(String browserName) {
        WebDriver driver = null;
        logger.info(browserName + " browser is selected for execution");
        switch (browserName.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
            driver = new FirefoxDriver();
            break;
            case "edge":
            driver = new EdgeDriver(); 
        
            default:
                logger.error("Unsupported browser: " + browserName);
                throw new IllegalArgumentException("Browser not supported: " + browserName);
        }
        return driver;
    }
    }
