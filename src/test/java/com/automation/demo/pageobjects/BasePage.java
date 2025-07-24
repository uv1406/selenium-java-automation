package com.automation.demo.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.automation.demo.utils.ConfigReader;
import com.automation.demo.utils.LoggerUtil;

import java.time.Duration;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
 // Added for common wait conditions
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException; // Make sure this is imported
import org.openqa.selenium.ElementNotInteractableException; // Good to catch this too for native clicks
import org.openqa.selenium.StaleElementReferenceException;

public abstract class BasePage { // Make it abstract as it's not meant to be instantiated directly
    protected WebDriver driver;
    protected WebDriverWait wait;

    private static final Logger logger = LoggerUtil.getLogger(BasePage.class);


    // Constructor
    public BasePage(WebDriver driver) {
        this.driver = driver;
        // It's good practice to set implicit wait to 0 in your DriverFactory
        // for predictable explicit waits. If not, consider adding it here as a safety.
        // driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        int explicitWaitSeconds = ConfigReader.getIntProperty("default.explicit.wait.seconds");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitSeconds));
        logger.debug("BasePage initialized with WebDriverWait for " + explicitWaitSeconds + " seconds.");
    }
    //Common methods for all page objects can be added here

    /**
     * Waits for an element to be visible on the page.
     *
     * @param locator The By locator of the element to wait for.
     * @return The WebElement once it is visible.
     */
    protected WebElement waitForElementVisible(WebElement element) {
        logger.info("Waiting for WebElement to be visible.");
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForElementVisible(WebElement element, int customTimeoutSeconds) {
        logger.info("Waiting for WebElement to be visible for " + customTimeoutSeconds + " seconds.");
        return new WebDriverWait(driver, Duration.ofSeconds(customTimeoutSeconds))
               .until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForElementClickable(WebElement element) {
        logger.info("Waiting for WebElement to be clickable.");
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement waitForElementClickable(WebElement element, int customTimeoutSeconds) {
        logger.info("Waiting for WebElement to be clickable for " + customTimeoutSeconds + " seconds.");
        return new WebDriverWait(driver, Duration.ofSeconds(customTimeoutSeconds))
               .until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void clickElement(WebElement element) {
        
        WebElement clickableElement = waitForElementClickable(element);
        try {
            logger.info("Clicking on WebElement using native click.");
            clickableElement.click();
        } catch (ElementClickInterceptedException e) {
            logger.warn("Native click intercepted: Attempting javascript click " + e.getMessage());
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickableElement);
        } catch (Exception e) {
            logger.error("Failed to click on WebElement: " + e.getMessage(), e);
            throw e;
        }
    }

    protected void clickElement(WebElement element, int customTimeoutSeconds) {
        
        WebElement clickableElement = waitForElementClickable(element, customTimeoutSeconds);
        logger.info("Clicking on WebElement with custom timeout.");
        clickableElement.click();
    }

    protected void enterText(WebElement element, String text) {
        
        WebElement visibleElement = waitForElementVisible(element);
        logger.info("Entering text '" + text + "' into WebElement.");
        visibleElement.clear();
        visibleElement.sendKeys(text);
    }

    protected void enterText(WebElement element, String text, int customTimeoutSeconds) {
        
        WebElement visibleElement = waitForElementVisible(element, customTimeoutSeconds);
        logger.info("Entering text '" + text + "' into WebElement with custom timeout.");
        visibleElement.clear();
        visibleElement.sendKeys(text);
    }

    protected String getElementText(WebElement element) {
        
        WebElement visibleElement = waitForElementVisible(element);
        String text = visibleElement.getText();
        logger.info("Getting text from WebElement - Text: " + text);
        return text;
    }

    protected boolean isElementDisplayed(WebElement element) {
        try {
            WebElement visibleElement = waitForElementVisible(element);
            boolean displayed = visibleElement.isDisplayed();
            logger.info("WebElement is displayed: " + displayed);
            return displayed;
        } catch (Exception e) {
            logger.warn("WebElement is not displayed: " + e.getMessage());
            return false;
        }
    }

    // New: Overload for scrolling into view directly with WebElement
    protected void scrollIntoView(WebElement element) {
         logger.info("Scrolling WebElement into view.");
         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Or your default wait time
        WebElement actualElement = wait.until(ExpectedConditions.visibilityOf(element)); // Wait for visibility
       logger.info("Scrolling WebElement into view: " + actualElement);
       ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", actualElement);
    }

    // New: Overload for getting attribute directly with WebElement
    protected String getElementAttribute(WebElement element, String attribute) {
        
        WebElement visibleElement = waitForElementVisible(element);
        String value = visibleElement.getAttribute(attribute);
        logger.info("Getting attribute '" + attribute + "' from WebElement - Value: " + value);
        return value;
    }

    // New: Overload for isElementEnabled directly with WebElement
    protected boolean isElementEnabled(WebElement element) {
        
        WebElement visibleElement = waitForElementVisible(element);
        boolean enabled = visibleElement.isEnabled();
        logger.info("WebElement is enabled: " + enabled);
        return enabled;
    }

    // New: Overload for isElementSelected directly with WebElement
    protected boolean isElementSelected(WebElement element) {
        
        WebElement visibleElement = waitForElementVisible(element);
        boolean selected = visibleElement.isSelected();
        logger.info("WebElement is selected: " + selected);
        return selected;
    }

    // New: Overload for selectCheckbox directly with WebElement
    protected void selectCheckbox(WebElement checkbox) {
        
        WebElement visibleCheckbox = waitForElementVisible(checkbox);
        if (!visibleCheckbox.isSelected()) {
            logger.info("Selecting checkbox WebElement.");
            visibleCheckbox.click();
        } else {
            logger.info("Checkbox WebElement already selected.");
        }
    }

    // New: Overload for deselectCheckbox directly with WebElement
    protected void deselectCheckbox(WebElement checkbox) {
        
        WebElement visibleCheckbox = waitForElementVisible(checkbox);
        if (visibleCheckbox.isSelected()) {
            logger.info("Deselecting checkbox WebElement.");
            visibleCheckbox.click();
        } else {
            logger.info("Checkbox WebElement already deselected.");
        }
    }

    // New: Overload for selectRadioButton directly with WebElement
    protected void selectRadioButton(WebElement radio) {
        
        WebElement visibleRadio = waitForElementVisible(radio);
        if (!visibleRadio.isSelected()) {
            logger.info("Selecting radio button WebElement.");
            visibleRadio.click();
        } else {
            logger.info("Radio button WebElement already selected.");
        }
    }
/**
     * Robust click method that first attempts a native Selenium click.
     * If the native click fails due to common interaction issues (like interception,
     * not interactable, or element being off-screen/not immediately clickable),
     * it falls back to scrolling the element into view and then performing a JavaScript click.
     *
     * @param element The WebElement to click (can be a PageFactory proxy).
     */
    protected void clickElementRobustly(By locator) {
        logger.info("Attempting to click WebElement robustly: " + locator);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            logger.info("Attempting native click on WebElement directly: " + locator);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));              
            element.click();
            logger.info("Native Click of WebElement is successful: " + locator);
            return; // Exit if native click is successful
        } catch (TimeoutException | ElementNotInteractableException | StaleElementReferenceException se) {
            logger.warn("Native click failed, attempting JavaScript click for WebElement: " + locator + ". Attempting JavaScript click.");
            try {
                 // Ensure the element is in view and resolved for JavaScript operations
                WebElement resolvedElement = findElementRobustly(locator);
                jsScrollIntoView(resolvedElement);
                // Perform JavaScript click
                logger.info("Performing JavaScript click on WebElement after fallback logic: " + resolvedElement);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", resolvedElement);
            } catch (Exception jsFallbackException) {
                logger.error("JavaScript click failed on WebElement: " + locator, jsFallbackException);
            }
            logger.info("JavaScript click successful on WebElement via fallback on: " + locator);
        }
        catch (Exception exception){
                // Catch any other unexpected exceptions that might occur during the initial native click attempt
            logger.error("Unexpected error during click on WebElement: " + locator, exception.getMessage());
            throw new RuntimeException("Failed to click on WebElement with both native and JavaScript methods: " + locator, exception);
        }
    }
    protected void jsScrollIntoView(WebElement element) {
        logger.info("Scrolling WebElement into view using JavaScript: " + element);

        try {
            
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            logger.info("WebElement scrolled into view.");

        } catch (Exception e) {
            logger.error("Failed to scroll WebElement into view or resolve: " + element + " - " + e.getMessage(), e);
            throw e; 
        }

} /**
     * Robustly finds an element using a By locator and waits for its presence.
     * This handles PageFactory proxy resolution implicitly if the By locator is used
     * directly for finding.
     *
     * @param locator The By locator for the element.
     * @return The resolved WebElement.
     */
    protected WebElement findElementRobustly(By locator) {
        logger.info("Finding WebElement robustly: " + locator);
        try {
            // First, try to find the element normally
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));        
        } catch (Exception e) {
            logger.error("Failed to find WebElement: " + locator + " - " + e.getMessage(), e);
            throw new RuntimeException("Failed to find WebElement: " + locator, e);
        }
    }

}
