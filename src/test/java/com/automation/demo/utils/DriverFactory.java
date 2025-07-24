



package com.automation.demo.utils;

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
        switch (browserName.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--start-maximized"); // Example option to start maximized
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--start-maximized"); // Example option to start maximized
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--start-maximized"); // Example option to start maximized
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                logger.error("Unsupported browser: " + browserName);
                throw new IllegalArgumentException("Browser not supported: " + browserName);
        }
        return driver;
    }

}
