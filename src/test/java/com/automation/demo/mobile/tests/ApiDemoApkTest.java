package com.automation.demo.mobile.tests;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options; // <-- Import Options

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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class ApiDemoApkTest {

    private AndroidDriver driver;
    private WebDriverWait wait;

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

        driver = new AndroidDriver(URI.create(appiumServerUrl).toURL(), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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
    @Test
    public void scrollAndTapTest(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement viewsButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
            AppiumBy.accessibilityId("Views")
        ));
        viewsButton.click();
        driver.findElement(AppiumBy.accessibilityId("Expandable Lists")).click();
        driver.findElement(AppiumBy.accessibilityId("1. Custom Adapter")).click();

        // Scroll down to the "Dog Names" option and tap it
        String targetText = "Dog Names";
        WebElement targetElement = null;
        for(int i=0; i<10;i++){
            List<WebElement> elements = driver.findElements(AppiumBy.xpath("//android.widget.TextView[@text='" + targetText + "']"));
            if(!elements.isEmpty()){
                targetElement = elements.get(0);
                break;
            }
            // Scroll down
            else{
                scrollDown();
            }
        }
      Assert.assertNotNull(targetElement,"Dog Names option not found");
      targetElement.click();
      System.out.println("Test Passed! The 'Dog Names' option is visible and will be clicked.");
    }

    private void scrollDown() {
        //Performing a scroll down gesture
        Dimension size = driver.manage().window().getSize();
        int startX= size.width/2;
        int startY= (int) (size.height * 0.8);
        int endY= (int) (size.height * 0.2);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(0), PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(swipe));



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
        // CORRECTED: Use a Pause object for the wait action
        dragAndDropSequence.addAction(new Pause(finger, Duration.ofMillis(500)));
        dragAndDropSequence.addAction(finger.createPointerMove(Duration.ofMillis(700), PointerInput.Origin.viewport(), targetCenter.getX(), targetCenter.getY()));
        dragAndDropSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        
        driver.perform(Collections.singletonList(dragAndDropSequence));
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