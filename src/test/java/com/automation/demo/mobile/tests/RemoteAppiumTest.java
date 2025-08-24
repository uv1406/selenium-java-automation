package com.automation.demo.mobile.tests;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class RemoteAppiumTest {

    private AndroidDriver driver;

    @BeforeTest
    public void setUp() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:deviceName", "Android Emulator");
        caps.setCapability("appium:automationName", "UiAutomator2");

        // --- UPDATED CAPABILITIES FOR THE CONTACTS APP ---
        caps.setCapability("appium:appPackage", "com.google.android.contacts");
        caps.setCapability("appium:appActivity", "com.android.contacts.activities.PeopleActivity");
        // -------------------------------------------------

        caps.setCapability("appium:noReset", true);

        String appiumServerUrl = "http://192.168.0.134:4723/";

        driver = new AndroidDriver(new URL(appiumServerUrl), caps);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    /**
     * This test launches the Contacts app and verifies the "Create new contact" button is visible.
     */
    @Test
    public void verifyCreateContactButtonIsVisibleTest() {
        // Find the floating action button to create a new contact.
        // Its ID is typically very consistent across versions.
        WebElement createContactButton = driver.findElement(AppiumBy.id("com.google.android.contacts:id/floating_action_button"));
        Assert.assertTrue(createContactButton.isDisplayed(), "The 'Create new contact' button was not found.");

        System.out.println("Test Passed! The 'Create new contact' button is visible.");
        
        // You can now click it to proceed
        createContactButton.click();
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
