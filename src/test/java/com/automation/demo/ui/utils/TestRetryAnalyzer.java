package com.automation.demo.ui.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.apache.logging.log4j.Logger;

public class TestRetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LoggerUtil.getLogger(TestRetryAnalyzer.class);
    private int retryCount = 0;
    private static int maxRetryCount;

   static {
        try {
            maxRetryCount = ConfigReader.getIntProperty("test.retry.count");
            logger.info("TestRetryAnalyzer initialized. Max retry count: " + maxRetryCount);
        } catch (Exception e) {
            maxRetryCount = 0;
            logger.error("Failed to read 'test.retry.count' from config.properties. Retries disabled. Error: " + e.getMessage());
        }
    }

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            logger.warn("Retrying test method '" + result.getName() + "' for the " + (retryCount + 1) + " time(s).");
            retryCount++;
            return true;
        }
        logger.info("Test method '" + result.getName() + "' will not be retried further after " + retryCount + " attempt(s).");
        return false;
    }
    
}
