package com.automation.demo.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

public class ScreenshotUtil {

    private static final Logger logger = LoggerUtil.getLogger(ScreenshotUtil.class);

    public static void takeScreenshot(WebDriver driver, String testName) {
        try {
            if (driver instanceof TakesScreenshot) {
                File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String screenshotDir = ConfigReader.getProperty("screenshot.directory");
                if (screenshotDir == null || screenshotDir.trim().isEmpty()) {
                    screenshotDir = "target/screenshots";
                    logger.warn("Screenshot directory not found in config.properties. Using default: " + screenshotDir);
                }

                File destDir = new File(screenshotDir);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                    logger.info("Created screenshot directory: " + destDir.getAbsolutePath());
                }

                File destinationFile = new File(destDir, testName + "_" + System.currentTimeMillis() + ".png");
                FileUtils.copyFile(scrFile, destinationFile);
                logger.info("Screenshot saved to: " + destinationFile.getAbsolutePath());
            } else {
                logger.warn("WebDriver does not support taking screenshots.");
            }
        } catch (Exception e) {
            logger.error("Failed to take screenshot for test '" + testName + "': " + e.getMessage(), e);
        }
    }
}