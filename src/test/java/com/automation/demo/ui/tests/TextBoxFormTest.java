package com.automation.demo.ui.tests;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.automation.demo.ui.data.TestDataProviders;
import com.automation.demo.ui.pageobjects.TextBoxPage;
import com.automation.demo.ui.utils.DriverManager;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TextBoxFormTest extends BaseTest {

    @Test(dataProvider = "formData", dataProviderClass = TestDataProviders.class, groups = {"smoke", "regression"})
    public void testTextBoxForm(String fullName, String email, String currentAddress, String permanentAddress) {
        try {
            // Open URL
            DriverManager.getDriver().get("https://demoqa.com/text-box");
            System.out.println("Navigated to: " + DriverManager.getDriver().getCurrentUrl());
            WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(5));

            // Create page object
            TextBoxPage page = new TextBoxPage(DriverManager.getDriver());

            // Perform actions
            page.fillFullName(fullName);
            page.fillEmail(email);
            page.fillCurrentAddress(currentAddress);
            page.fillPermanentAddress(permanentAddress);
            page.clickSubmit();

            // Wait and assert
            wait.until(ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.id("output")));
            String output = page.getOutputText();

            Assert.assertTrue(output.contains(fullName), "Full name not found in output");
            Assert.assertTrue(output.contains(email), "Email not found in output");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test failed due to exception", e); // Re-throw to make TestNG fail the test
        }
    }
}
    
    