package com.automation.demo.ui.utils;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions; // Import base class for options
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

// Optional: Use WebDriverManager dependency for local driver setup
// import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {

    private static final Logger logger = LoggerUtil.getLogger(DriverFactory.class);

    /**
     * Creates a WebDriver instance based on configuration properties.
     * Handles both local and remote execution modes.
     *
     * @return WebDriver instance.
     * @throws MalformedURLException If the selenium.grid.url is invalid in remote mode.
     * @throws IllegalArgumentException If the browser is unsupported or grid URL is missing.
     */
    public static WebDriver createDriver() throws MalformedURLException, URISyntaxException {
        String runMode = ConfigReader.getProperty("run.mode", "local"); // Default to local
        String browserName = ConfigReader.getProperty("browser", "chrome").toLowerCase(); // Default to chrome
        boolean isHeadless = Boolean.parseBoolean(ConfigReader.getProperty("run.headless", "false")); // Default to false

        logger.info("Selected Run Mode: {}", runMode);
        logger.info("Selected Browser: {}", browserName);
        logger.info("Headless Mode: {}", isHeadless);

        WebDriver driver;

        if ("remote".equalsIgnoreCase(runMode)) {
            driver = createRemoteDriver(browserName, isHeadless);
        } else {
            driver = createLocalDriver(browserName, isHeadless);
        }
        return driver;
    }

    /**
     * Creates a local WebDriver instance.
     */
    private static WebDriver createLocalDriver(String browserName, boolean isHeadless) {
        WebDriver driver;
        logger.info("Creating local driver for: {}", browserName);
        switch (browserName) {
            case "chrome":
                // Optional: WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = getChromeOptions(isHeadless);
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                // Optional: WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = getFirefoxOptions(isHeadless);
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                // Optional: WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = getEdgeOptions(isHeadless);
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                logger.error("Unsupported browser for local execution: {}", browserName);
                throw new IllegalArgumentException("Local browser not supported: " + browserName);
        }
        return driver;
    }

    /**
     * Creates a RemoteWebDriver instance connecting to the Selenium Grid.
     */
    private static WebDriver createRemoteDriver(String browserName, boolean isHeadless) throws MalformedURLException ,URISyntaxException {
        String gridUrl = ConfigReader.getProperty("selenium.grid.url");
        if (gridUrl == null || gridUrl.trim().isEmpty()) {
            logger.error("selenium.grid.url is not configured for remote execution.");
            throw new IllegalArgumentException("Selenium Grid URL is required for remote mode.");
        }
        URL hubUrl = new URI(gridUrl).toURL();
        AbstractDriverOptions<?> options; // Use the base options class

        logger.info("Creating remote driver for: {} on Grid: {}", browserName, gridUrl);
        switch (browserName) {
            case "chrome":
                options = getChromeOptions(isHeadless);
                break;
            case "firefox":
                options = getFirefoxOptions(isHeadless);
                break;
            case "edge":
                options = getEdgeOptions(isHeadless);
                break;
            default:
                logger.error("Unsupported browser for remote execution: {}", browserName);
                throw new IllegalArgumentException("Remote browser not supported: " + browserName);
        }
        // Create RemoteWebDriver with the Hub URL and appropriate browser options
        return new RemoteWebDriver(hubUrl, options);
    }

    // --- Browser Options Helper Methods ---

    private static ChromeOptions getChromeOptions(boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Start maximized locally
        // options.addArguments("--disable-infobars");
        // options.addArguments("--disable-extensions");
        // options.addArguments("--no-sandbox"); // Often needed in Docker/CI
        // options.addArguments("--disable-dev-shm-usage"); // Often needed in Docker/CI
        if (isHeadless) {
            logger.info("Configuring Chrome for headless mode.");
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080"); // Important for headless consistency
        }
        return options;
    }

    private static FirefoxOptions getFirefoxOptions(boolean isHeadless) {
        FirefoxOptions options = new FirefoxOptions();
        // Firefox doesn't have a direct start-maximized argument like Chrome
        // Maximizing is usually done via driver.manage().window().maximize() later
        if (isHeadless) {
            logger.info("Configuring Firefox for headless mode.");
            options.addArguments("-headless");
             options.addArguments("--window-size=1920,1080");
        }
        return options;
    }

    private static EdgeOptions getEdgeOptions(boolean isHeadless) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        if (isHeadless) {
            logger.info("Configuring Edge for headless mode.");
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        return options;
    }
}