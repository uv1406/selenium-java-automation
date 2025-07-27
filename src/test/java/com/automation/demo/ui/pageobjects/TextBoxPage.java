package com.automation.demo.ui.pageobjects;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.automation.demo.ui.utils.LoggerUtil;
import org.apache.logging.log4j.Logger; // Import Log4j2 Logger

public class TextBoxPage extends BasePage { 
    private static final Logger logger = LoggerUtil.getLogger(TextBoxPage.class);
    

    // Constructor
    public TextBoxPage(WebDriver driver) {
        super(driver); // Call the constructor of BasePage (which initializes 'wait')
        // *** Initialize Page Factory elements ***
        // This line tells PageFactory to create proxies for all @FindBy annotated WebElements
        PageFactory.initElements(driver, this);
        logger.debug("TextBoxPage initialized with WebDriver using PageFactory.");
    }

    // Locators
      // Page Factory Locators using @FindBy annotation on WebElement fields
    @FindBy(id = "userName")
    private WebElement fullNameField;

    @FindBy(id = "userEmail")
    private WebElement emailField;

    @FindBy(id = "currentAddress")
    private WebElement currentAddressField;

    @FindBy(id = "permanentAddress")
    private WebElement permanentAddressField;

    @FindBy(id = "submit")
    private WebElement submitButton;

    @FindBy(id = "output")
    private WebElement outputBox;

    // Actions
    public void fillFullName(String name) {
        enterText(fullNameField, name);
    }

    public void fillEmail(String email) {
        logger.info("Entering email: " + email);
        enterText(emailField, email);
    }

    public void fillCurrentAddress(String address) {
        enterText(currentAddressField, address);
    }

    public void fillPermanentAddress(String address) {
        enterText(permanentAddressField, address);
    }

    public void clickSubmit() {
        scrollIntoView(submitButton);
        clickElement(submitButton);
    }

    public String getOutputText() {
        return getElementText(outputBox);
    }
}