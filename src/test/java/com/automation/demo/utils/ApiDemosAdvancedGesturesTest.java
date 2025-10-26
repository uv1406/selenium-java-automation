package com.automation.demo.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

public class ApiDemosAdvancedGesturesTest {
    private AndroidDriver driver;
    private WebDriverWait wait;

    @BeforeTest
    public void setUp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName("Android Emulator");
        options.setAutomationName("UiAutomator2");
        options.setAppPackage("io.appium.android.apis");
        options.setAppActivity(".ApiDemos");
        
        String appiumServerUrl = "http://192.168.0.134:4723/";
        driver = new AndroidDriver(URI.create(appiumServerUrl).toURL(), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    public void dragAndDropTest() {
        // 1. Navigate to the Drag and Drop screen
        System.out.println("Navigating to Drag and Drop example...");
        driver.findElement(AppiumBy.accessibilityId("Views")).click();
        driver.findElement(AppiumBy.accessibilityId("Drag and Drop")).click();

        // 2. Locate the source and target elements
        System.out.println("Locating source and target elements...");
        WebElement sourceDot = wait.until(ExpectedConditions.visibilityOfElementLocated(
            AppiumBy.id("io.appium.android.apis:id/drag_dot_1")
        ));
        WebElement targetDot = driver.findElement(AppiumBy.id("io.appium.android.apis:id/drag_dot_2"));

        // 3. Perform the drag and drop action
        System.out.println("Performing drag and drop...");
        dragAndDrop(sourceDot, targetDot);

        // 4. Verify the result
        System.out.println("Verifying drag and drop result...");
        WebElement resultText = wait.until(ExpectedConditions.visibilityOfElementLocated(
            AppiumBy.id("io.appium.android.apis:id/drag_result_text")
        ));

        Assert.assertEquals(resultText.getText(), "Dropped!", "The drag and drop operation failed!");
        System.out.println("Verification successful! Text is 'Dropped!'");
    }

    @Test
    public void swipeLeftTest() {
        System.out.println("Navigating to Gallery for swipe test...");
        driver.findElement(AppiumBy.accessibilityId("Views")).click();

        // Use Android's built-in UIAutomator to scroll to the "Gallery" item
        WebElement galleryMenu = driver.findElement(AppiumBy.androidUIAutomator(
            "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView("
            + "new UiSelector().text(\"Gallery\"));"));
        galleryMenu.click();
        
        driver.findElement(AppiumBy.accessibilityId("1. Photos")).click();
        
        // 1. Verify the first image is focused
        List<WebElement> images = driver.findElements(AppiumBy.className("android.widget.ImageView"));
        Assert.assertEquals(images.get(0).getAttribute("focusable"), "true", "First image should be focused initially.");
        System.out.println("First image is correctly focused.");

        // 2. Perform the swipe
        swipeLeft();

        // 3. Verify the second image is now focused
        Assert.assertEquals(images.get(1).getAttribute("focusable"), "true", "Second image should be focused after swipe.");
        System.out.println("Swipe successful! Second image is now focused.");
    }

    /**
     * Reusable method to drag one element to another's location.
     * @param source The element to drag.
     * @param target The element to drop onto.
     */
    private void dragAndDrop(WebElement source, WebElement target) {
        // Get the center of the source element
        Point sourceCenter = getCenterOfElement(source.getLocation(), source.getSize());
        // Get the center of the target element
        Point targetCenter = getCenterOfElement(target.getLocation(), target.getSize());
        
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence dragAndDropSequence = new Sequence(finger, 1);
        
        dragAndDropSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), sourceCenter.getX(), sourceCenter.getY()));
        dragAndDropSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        // Wait a small amount of time to simulate a "hold"
        dragAndDropSequence.addAction(new Pause(finger, Duration.ofMillis(500)));
        dragAndDropSequence.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), targetCenter.getX(), targetCenter.getY()));
        dragAndDropSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Collections.singletonList(dragAndDropSequence));
    }

    /**
     * Reusable method to swipe left on the screen.
     */
    private void swipeLeft() {
        System.out.println("Performing a swipe left gesture...");
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.8);
        int endX = (int) (size.width * 0.2);
        int startY = size.height / 2;

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), endX, startY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));
    }
    
    /**
     * Helper method to calculate the center coordinates of an element.
     * @param location The top-left Point of the element.
     * @param size The Dimension of the element.
     * @return The center Point of the element.
     */
    private Point getCenterOfElement(Point location, Dimension size) {
        return new Point(location.getX() + size.getWidth() / 2, location.getY() + size.getHeight() / 2);
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

