package com.automation.demo.ui.tests;



import com.automation.demo.ui.base.BaseTest;
import com.automation.demo.ui.pageobjects.CheckBoxPage;
import com.automation.demo.ui.utils.DriverManager;

import io.qameta.allure.Story;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckBoxTest extends BaseTest {
    @Story("CheckBox Selection Test")
    @Test(groups = {"smoke", "regression"})
    public void testCheckBox() {
        try {
            // Open URL
            DriverManager.getDriver().get("https://demoqa.com/checkbox");
            System.out.println("Navigated to: " + DriverManager.getDriver().getCurrentUrl());
           // WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(5));

            // Create page object
            CheckBoxPage page = new CheckBoxPage(DriverManager.getDriver());

            // Perform actions
            page.selectHomeCheckBox();
            page.getHomeCheckBoxText();

            // Wait and assert
         
            String output = page.getHomeCheckBoxText();

            Assert.assertTrue(output.toLowerCase().contains("selected"), "Home CheckBox is not selected");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test failed due to exception", e); // Re-throw to make TestNG fail the test
        }
    }
}
    
    