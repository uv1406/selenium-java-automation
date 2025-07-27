



package com.automation.demo.ui.utils;

import org.apache.logging.log4j.Logger; // Import Log4j2 Logger
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.chrome.ChromeOptions; // For browser options

public class DriverFactory {

    private static final Logger logger = LoggerUtil.getLogger(DriverFactory.class);

    public static WebDriver createDriver(String browserName) {
        WebDriver driver = null;
        logger.info(browserName + " browser is selected for execution");
         // Read headless preference from config.properties
         boolean isHeadless = Boolean.parseBoolean(ConfigReader.getProperty("run.headless"));
         logger.info("Running in headless mode: " + isHeadless);
        switch (browserName.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized"); // Example option to start maximized
                if (isHeadless) {
                    chromeOptions.addArguments("--headless=new"); // Use --headless=new for newer Chrome versions
                    chromeOptions.addArguments("--disable-gpu"); // Recommended for headless
                    chromeOptions.addArguments("--window-size=1920,1080"); // Set a default window size for headless
                    logger.info("Chrome will run in headless mode.");
                }
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            if (isHeadless) {
                logger.info("Firefox will run in headless mode.");
                
                firefoxOptions.addArguments("--headless"); // Enable headless mode for Firefox
                firefoxOptions.addArguments("--start-maximized"); // Example option to start maximized
                } else {
                logger.info("Firefox will run in normal mode.");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                
                EdgeOptions edgeOptions = new EdgeOptions();
                if (isHeadless) {
                    edgeOptions.addArguments("--headless=new"); // Edge headless argument
                    edgeOptions.addArguments("--disable-gpu");
                    edgeOptions.addArguments("--window-size=1920,1080");
                    logger.info("Edge will run in headless mode.");
                }
                edgeOptions.addArguments("--no-sandbox"); // Recommended for CI environments
                edgeOptions.addArguments("--disable-dev-shm-usage"); // Recommended for CI environments
                driver = new EdgeDriver(edgeOptions);
                logger.debug("EdgeDriver instance created.");
                break;
            default:
                logger.error("Unsupported browser: " + browserName);
                throw new IllegalArgumentException("Browser not supported: " + browserName);
        }
        return driver;
    }

}
