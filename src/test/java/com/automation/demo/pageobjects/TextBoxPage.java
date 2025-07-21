package com.automation.demo.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


import com.automation.demo.utils.LoggerUtil; // Import your LoggerUtil
import org.apache.logging.log4j.Logger; // Import Log4j2 Logger

public class TextBoxPage extends BasePage { 
    private static final Logger logger = LoggerUtil.getLogger(TextBoxPage.class);
    

    // Constructor
    public TextBoxPage(WebDriver driver) {
        super(driver); // Call the constructor of BasePage
        logger.debug("TextBoxPage initialized with WebDriver.");
    }

    // Locators
    private By fullNameField = By.id("userName");
    private By emailField = By.id("userEmail");
    private By currentAddressField = By.id("currentAddress");
    private By permanentAddressField = By.id("permanentAddress");
    private By submitButton = By.id("submit");
    private By outputBox = By.id("output");

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