package com.automation.demo.mobile.tests;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options; // <-- Import Options
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class ApiDemoApkTest {

    private AndroidDriver driver;

    @BeforeTest
    public void setUp() throws MalformedURLException {
        // 1. Use UiAutomator2Options for modern, type-safe capabilities
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName("Android Emulator");
        options.setAutomationName("UiAutomator2");
        options.setAppPackage("io.appium.android.apis");
        options.setAppActivity("io.appium.android.apis.ApiDemos");
        options.setNoReset(true);

        // This URL can be externalized to a config file in a larger project
        String appiumServerUrl = "http://192.168.0.134:4723/";

        driver = new AndroidDriver(new URL(appiumServerUrl), options);
        // 2. Removed the implicit wait
    }

    @Test
    public void preferencesTest() {
        // Use an explicit wait to find the element
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement preferenceButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
            AppiumBy.accessibilityId("Preference")
        ));
        
        // 3. The wait above confirms visibility, so a separate assertion is not needed.
        // We can proceed directly to the action.
        System.out.println("Test Passed! The 'Preference' button is visible and will be clicked.");
        
        preferenceButton.click();
        
        // You could add further steps here, like waiting for the next screen and asserting something on it.
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}