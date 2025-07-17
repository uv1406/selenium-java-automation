    package com.automation.demo;  

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automation.demo.pageobjects.TextBoxPage;
import com.automation.demo.tests.BaseTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test; // Import JUnit 5 Test annotation


    public class TextBoxFormTest extends BaseTest { // Class name can remain AppTest

        // This is a JUnit test method, it will be discovered and run by Maven's 'test' goal
        @Test
        public void testSeleniumFormSubmission() {
           
            try {       
                    

                // Open URL
                driver.get("https://demoqa.com/text-box");
                System.out.println("Navigated to: " + driver.getCurrentUrl());
                 WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

              
              // Create page object
            TextBoxPage page = new TextBoxPage(driver);

            // Perform actions
            page.fillFullName("Ujjawal Verma");
            page.fillEmail("ujjawal@example.com");
            page.fillCurrentAddress("123 Test Lane");
            page.fillPermanentAddress("456 Automation St");
            page.clickSubmit();

            // Wait and assert
            wait.until(ExpectedConditions.visibilityOfElementLocated(org.openqa.selenium.By.id("output")));
            String output = page.getOutputText();

           assertTrue(output.contains("Ujjawal Verma"), "Full name not found in output");

            assertTrue(output.contains("ujjawal@example.com"),"Email not found in output");


            } catch (Exception e) {
                System.err.println("Test failed: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Test failed due to exception", e); // Re-throw to make JUnit fail the test
            } 
            }
        }
    
    