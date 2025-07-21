package com.automation.demo.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.automation.demo.utils.ConfigReader;
import com.automation.demo.utils.LoggerUtil;

import java.time.Duration;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By; // Added for common wait conditions
import org.openqa.selenium.support.ui.ExpectedConditions; // Added for common wait conditions

import com.automation.demo.utils.LoggerUtil; // Import your LoggerUtil
import org.apache.logging.log4j.Logger; // Import Log4j2 Logger

public abstract class BasePage { // Make it abstract as it's not meant to be instantiated directly
    protected WebDriver driver;
    protected WebDriverWait wait;

    private static final Logger logger = LoggerUtil.getLogger(BasePage.class);


    // Constructor
    public BasePage(WebDriver driver) {
        this.driver = driver;
        int implicitWaitSeconds = ConfigReader.getIntProperty("default.implicit.wait.seconds");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(implicitWaitSeconds));
        logger.debug("BasePage initialized with WebDriverWait for " + implicitWaitSeconds + " seconds.");
    }
    //Common methods for all page objects can be added here

    /**
     * Waits for an element to be visible on the page.
     *
     * @param locator The By locator of the element to wait for.
     * @return The WebElement once it is visible.
     */
    protected WebElement waitForElementVisible(By locator) {
        logger.info("Waiting for element to be visible: " + locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }   
    protected WebElement waitForElementClickable(By locator) {
        logger.info("Waiting for element to be clickable: " + locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    protected void clickElement(By locator) {
        WebElement element = waitForElementClickable(locator);
        logger.info("Clicking on element: " + locator);
        element.click();
    }   
    protected void enterText(By locator, String text) {
        WebElement element = waitForElementVisible(locator);
        logger.info("Entering text '" + text + "' into element: " + locator);
        element.clear();
        element.sendKeys(text);
    }
    protected String getElementText(By locator) {
        WebElement element = waitForElementVisible(locator);
        String text = element.getText();
        logger.info("Getting text from element: " + locator + " - Text: " + text);
        return text;
    }
    protected boolean isElementDisplayed(By locator) {
        try {
            WebElement element = waitForElementVisible(locator);
            boolean displayed = element.isDisplayed();
            logger.info("Element " + locator + " is displayed: " + displayed);
            return displayed;
        } catch (Exception e) {
            logger.warn("Element " + locator + " is not displayed: " + e.getMessage());
            return false;
        }
    }

    // Waits for an element to be invisible
    protected boolean waitForElementInvisible(By locator) {
        logger.info("Waiting for element to be invisible: " + locator);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // Gets an attribute value from an element
    protected String getElementAttribute(By locator, String attribute) {
        WebElement element = waitForElementVisible(locator);
        String value = element.getAttribute(attribute);
        logger.info("Getting attribute '" + attribute + "' from element: " + locator + " - Value: " + value);
        return value;
    }

    // Checks if an element is enabled
    protected boolean isElementEnabled(By locator) {
        WebElement element = waitForElementVisible(locator);
        boolean enabled = element.isEnabled();
        logger.info("Element " + locator + " is enabled: " + enabled);
        return enabled;
    }

    // Checks if an element is selected (for checkboxes/radio buttons)
    protected boolean isElementSelected(By locator) {
        WebElement element = waitForElementVisible(locator);
        boolean selected = element.isSelected();
        logger.info("Element " + locator + " is selected: " + selected);
        return selected;
    }

    // Selects a checkbox if not already selected
    protected void selectCheckbox(By locator) {
        WebElement checkbox = waitForElementVisible(locator);
        if (!checkbox.isSelected()) {
            logger.info("Selecting checkbox: " + locator);
            checkbox.click();
        } else {
            logger.info("Checkbox already selected: " + locator);
        }
    }

    // Deselects a checkbox if selected
    protected void deselectCheckbox(By locator) {
        WebElement checkbox = waitForElementVisible(locator);
        if (checkbox.isSelected()) {
            logger.info("Deselecting checkbox: " + locator);
            checkbox.click();
        } else {
            logger.info("Checkbox already deselected: " + locator);
        }
    }

    // Selects a radio button
    protected void selectRadioButton(By locator) {
        WebElement radio = waitForElementVisible(locator);
        if (!radio.isSelected()) {
            logger.info("Selecting radio button: " + locator);
            radio.click();
        } else {
            logger.info("Radio button already selected: " + locator);
        }
    }

    // Gets a list of elements
    protected java.util.List<WebElement> getElements(By locator) {
        logger.info("Getting list of elements for locator: " + locator);
        return driver.findElements(locator);
    }

    // Waits for a specific text to be present in an element
    protected boolean waitForTextInElement(By locator, String text) {
        logger.info("Waiting for text '" + text + "' to be present in element: " + locator);
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    // Waits for an element to be present in the DOM
    protected WebElement waitForElementPresent(By locator) {
        logger.info("Waiting for element to be present in DOM: " + locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // Scrolls to an element (if needed)
    protected void scrollToElement(By locator) {
        WebElement element = waitForElementVisible(locator);
        logger.info("Scrolling to element: " + locator);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Submits a form element
    protected void submitForm(By locator) {
        WebElement element = waitForElementVisible(locator);
        logger.info("Submitting form for element: " + locator);
        element.submit();
    }
    protected void scrollIntoView(By locator) {
        WebElement element = waitForElementVisible(locator);
        logger.info("Scrolling into view for element: " + locator);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }
}