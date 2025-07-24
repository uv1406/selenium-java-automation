package com.automation.demo.pageobjects;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;

import com.automation.demo.utils.LoggerUtil; // Import your LoggerUtil
import org.apache.logging.log4j.Logger; // Import Log4j2 Logger

public class CheckBoxPage extends BasePage {
    private static final Logger logger = LoggerUtil.getLogger(CheckBoxPage.class);
    

    // Constructor
    public CheckBoxPage(WebDriver driver) {
        super(driver); // Call the constructor of BasePage (which initializes 'wait')
        // *** Initialize Page Factory elements ***
        // This line tells PageFactory to create proxies for all @FindBy annotated WebElements
        PageFactory.initElements(driver, this);
        logger.debug("CheckBoxPage initialized with WebDriver using PageFactory.");
    }

    // Locators
      // Page Factory Locators using @FindBy annotation on WebElement fields
    @FindBy(id = "tree-node-home")
    private WebElement HomeCheckBox;

    private By homeCheckBoxLocator = By.id("tree-node-home");

    @FindBy(id = "result")
    private WebElement HomeSelectedText;


    // Actions
    public void selectHomeCheckBox() {
        clickElementRobustly(homeCheckBoxLocator);
    }
    public String getHomeCheckBoxText() {
        logger.info("Getting text from Home CheckBox.");
        return getElementText(HomeSelectedText);
    }   
}