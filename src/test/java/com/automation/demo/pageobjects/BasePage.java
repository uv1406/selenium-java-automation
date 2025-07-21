package com.automation.demo.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.automation.demo.utils.ConfigReader;
import com.automation.demo.utils.LoggerUtil;

import java.time.Duration;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions; // Added for common wait conditions


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
        logger.info("Clicking on WebElement.");
        clickableElement.click();
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
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
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

    // New: Overload for submitForm directly with WebElement
    protected void submitForm(WebElement element) {
        WebElement visibleElement = waitForElementVisible(element);
        logger.info("Submitting form for WebElement.");
        visibleElement.submit();
    }
}
