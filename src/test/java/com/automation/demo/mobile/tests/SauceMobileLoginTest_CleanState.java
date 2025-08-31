package com.automation.demo.mobile.tests;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class SauceMobileLoginTest_CleanState {
    private AndroidDriver driver;

    @BeforeTest
    public void setUp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName("Android Emulator");
        options.setAutomationName("UiAutomator2");
        options.setAppPackage("com.saucelabs.mydemoapp.rn");
        options.setAppActivity("com.saucelabs.mydemoapp.rn.MainActivity");
        
        // --- KEY CHANGE ---
        // Set noReset to false to clear app data before each session.
        // This ensures the app is always in a logged-out state when the test starts.
        options.setNoReset(false); 
        // We no longer need forceAppLaunch because a full reset handles it.

        String appiumServerUrl = "http://192.168.0.134:4723/";
        driver = new AndroidDriver(URI.create(appiumServerUrl).toURL(), options);
        System.out.println("Driver initialized successfully with a clean app state.");
    }

    @Test
    public void loginTest() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        
        // User flow remains the same:
        // 1. Click the hamburger menu
        System.out.println("Waiting for hamburger menu...");
        WebElement hamburgerMenuElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
            AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"open menu\"]/android.widget.ImageView")
        ));
        hamburgerMenuElement.click();
        
        // 2. Click the "Log In" option
        System.out.println("Waiting for 'Log In' menu item...");
        WebElement loginMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(
            AppiumBy.xpath("//android.widget.TextView[@text=\"Log In\"]")
        ));
        loginMenu.click();

        // 3. Enter credentials
        System.out.println("Waiting for username input field...");
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            AppiumBy.accessibilityId("Username input field")
        ));
        WebElement passwordInput = driver.findElement(AppiumBy.accessibilityId("Password input field"));
        
        usernameInput.clear();
        usernameInput.sendKeys("bob@example.com");
        passwordInput.clear();
        passwordInput.sendKeys("10203040");

        // 4. Click login
        WebElement loginButton = driver.findElement(AppiumBy.accessibilityId("Login button"));
        loginButton.click();

        // 5. Verify successful login
        System.out.println("Verifying login success...");
        WebElement productsHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(
            AppiumBy.xpath("//android.widget.TextView[@text='Products']")
        ));
        Assert.assertTrue(productsHeader.isDisplayed(), "Login failed: Products page header not found.");
        System.out.println("Login Successful! Products page is displayed.");
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            System.out.println("Closing the driver session.");
            driver.quit();
        }
    }
}
