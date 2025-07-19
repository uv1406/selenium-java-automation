package com.automation.demo.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;



public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Uncomment if you want headless execution
        // options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        System.out.println("Browser launched for the thread:" + Thread.currentThread().threadId());
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed.");
            System.out.println("Browser Closed for the thread:" + Thread.currentThread().threadId());
        }
        System.out.println("Teardown finished in thread: " + Thread.currentThread().threadId());
    }
}
