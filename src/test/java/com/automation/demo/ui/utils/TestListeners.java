package com.automation.demo.ui.utils;

import io.qameta.allure.Attachment; // Allure attachment annotation
import org.apache.logging.log4j.Logger; // Log4j2 Logger
import org.openqa.selenium.WebDriver; // Selenium WebDriver
import org.testng.ITestContext; // TestNG Interfaces
import org.testng.ITestListener; // TestNG Interfaces
import org.testng.ITestResult; // TestNG Interfaces

// This class implements ITestListener to hook into TestNG test lifecycle events
public class TestListeners implements ITestListener {

    // Initialize Log4j2 logger for this class
    private static final Logger logger = LoggerUtil.getLogger(TestListeners.class);

    /**
     * Called when the test suite starts.
     * Logs the start of the test suite.
     * @param context The TestNG test context.
     */
    @Override
    public void onStart(ITestContext context) {
        logger.info("---------- Test Suite '" + context.getSuite().getName() + "' Started ----------");
    }

    /**
     * Called when the test suite finishes.
     * Logs the completion of the test suite.
     * @param context The TestNG test context.
     */
    @Override
    public void onFinish(ITestContext context) {
        logger.info("---------- Test Suite '" + context.getSuite().getName() + "' Finished ----------");
    }

    /**
     * Called when an individual test method starts.
     * Logs the name of the started test method.
     * @param result The TestNG test result object.
     */
    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Test Started: " + result.getName());
    }

    /**
     * Called when an individual test method passes successfully.
     * Logs the name of the passed test method.
     * @param result The TestNG test result object.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Test Passed: " + result.getName());
        // Optional: You could attach a screenshot here for successful tests if needed,
        // or attach specific successful logs.
        // For example: attachLog(result.getName() + " - Test Passed Log");
    }

    /**
     * Called when an individual test method fails.
     * Logs the failure, attempts to capture and attach a screenshot to Allure.
     * @param result The TestNG test result object.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Test Failed: " + result.getName());
        // Log the exception message if available
        if (result.getThrowable() != null) {
            logger.error("Reason: " + result.getThrowable().getMessage());
        } else {
            logger.error("Reason: Unknown failure cause (no throwable)");
        }

        // Get the WebDriver instance associated with the current thread
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            try {
                // Use ScreenshotUtil to get screenshot bytes
                byte[] screenshotBytes = ScreenshotUtil.takeScreenshotAsBytes(driver);

                // Attach the screenshot bytes to the Allure report if bytes were captured
                if (screenshotBytes != null && screenshotBytes.length > 0) {
                    attachScreenshot("Screenshot on Failure: " + result.getName(), screenshotBytes);
                    logger.debug("Screenshot attached for failed test: " + result.getName());
                } else {
                    logger.warn("No screenshot bytes captured for failed test: " + result.getName() + ". Check ScreenshotUtil.");
                }

                // Optional: You could also save a copy to file using ScreenshotUtil.takeScreenshot(driver, result.getName());
                // for local debugging purposes, in addition to attaching to Allure.
                
            } catch (Exception e) {
                logger.error("Failed to process screenshot for " + result.getName() + ": " + e.getMessage());
            }
        } else {
            logger.warn("WebDriver instance was null for failed test: " + result.getName() + ". Cannot capture screenshot.");
        }
        // You can also attach system logs or specific log files here if configured in log4j2.xml
    }

    /**
     * Called when an individual test method is skipped.
     * Logs the name of the skipped test method.
     * @param result The TestNG test result object.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("Test Skipped: " + result.getName());
        // Optional: Attach logs or other info for skipped tests
    }

    /**
     * Called when a test method fails but is within the success percentage.
     * (Less commonly used in typical automation frameworks).
     * @param result The TestNG test result object.
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("Test failed but within success percentage: " + result.getName());
    }

    /**
     * Allure-specific method to attach a screenshot to the report.
     * The `@Attachment` annotation makes this method's return value available as an attachment in Allure.
     * The `value = "{0}"` uses the first parameter (screenshotName) as the attachment's display name.
     * `type = "image/png"` specifies the MIME type for the attachment.
     *
     * @param screenshotName The name to display for the screenshot in the report.
     * @param screenshotBytes The byte array of the screenshot.
     * @return The byte array, which Allure will then process.
     */
    @Attachment(value = "{0}", type = "image/png")
    public byte[] attachScreenshot(String screenshotName, byte[] screenshotBytes) {
        return screenshotBytes;
    }

    /**
     * Optional Allure-specific method to attach plain text content (e.g., log snippets).
     *
     * @param logMessage The text message to attach.
     * @return The text message as a String.
     */
    @Attachment(value = "{0}", type = "text/plain")
    public String attachLog(String logMessage) {
        return logMessage;
    }
}










