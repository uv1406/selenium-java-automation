package com.automation.demo.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TextBoxPage { 
    private WebDriver driver;

    // Constructor
    public TextBoxPage(WebDriver driver) {
        this.driver = driver;
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
        driver.findElement(fullNameField).sendKeys(name);
    }

    public void fillEmail(String email) {
        driver.findElement(emailField).sendKeys(email);
    }

    public void fillCurrentAddress(String address) {
        driver.findElement(currentAddressField).sendKeys(address);
    }

    public void fillPermanentAddress(String address) {
        driver.findElement(permanentAddressField).sendKeys(address);
    }

    public void clickSubmit() {
        WebElement submit = driver.findElement(submitButton);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submit);
        submit.click();
    }

    public String getOutputText() {
        return driver.findElement(outputBox).getText();
    }
}