package com.automation.demo.ui.tests;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.*;
import org.openqa.selenium.WebElement;

import com.automation.demo.ui.base.BaseTest;
import com.automation.demo.ui.data.TestDataProviders;
import com.automation.demo.ui.pageobjects.TextBoxPage;
import com.automation.demo.ui.utils.ConfigReader;
import com.automation.demo.ui.utils.DriverManager;
import com.mysql.cj.x.protobuf.MysqlxCrud.Find;

import java.io.ObjectInputFilter.Config;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RandomPractice {
    WebDriver driver;

    @Test
    public void sampleMethod() {
        try {
               ChromeOptions options = new ChromeOptions();
       options.addArguments("start-maximized");
       driver = new ChromeDriver(options);
       WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      
       driver.get(ConfigReader.getProperty("WEB_URL"));
        System.out.println("This is a sample method in RandomPractice class.");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(ConfigReader.getProperty("WEB_USER"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password"))).sendKeys(ConfigReader.getProperty("WEB_PASS"));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button"))).click();
        System.out.println("Login button clicked. Navigated to the inventory page.");

        //Get all the product names on the page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_item_name")));
        List<WebElement> nameElements = driver.findElements(By.className("inventory_item_name"));

        //Stream to transform the list of WebElements to a list of Strings
        List<String> productNames = nameElements.stream()
                .map(WebElement::getText)
                .toList();
        System.out.println("Product Names: " + productNames);
        Assert.assertTrue(productNames.contains("Sauce Labs Backpack"), "Product not found!");
            //Find the product named "Sauce Labs Fleece Jacket" and click its "Add to Cart" button.
   System.out.println("Attempting to add 'Sauce Labs Fleece Jacket' to the cart...");
   wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inventory_container")));
   String targetProduct = "Sauce Labs Fleece Jacket";
   List<WebElement> allProducts = driver.findElements(By.className("inventory_item"));
   //Stream to filter the product and click its button
   WebElement targetItem = allProducts.stream()
            .filter(item -> {
                String name = item.findElement(By.className("inventory_item_name")).getText();
                return name.equals(targetProduct);
            })
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Product not found: " + targetProduct));

            // 4. Assert that we found the parent container
            Assert.assertNotNull(targetItem, "Parent container for the product not found.");
            System.out.println("Parent container for the product found.");
            // 5. Now, find the button *within* that specific container
//    We can use a simpler selector because we're already in the right scope.

            WebElement addToCartButton = targetItem.findElement(By.tagName("button"));
            wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
            System.out.println("'Add to Cart' button clicked for " + targetProduct);

            //Get a List<Double> of all product prices that are less than $20.
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_item")));
            List<WebElement> priceElements = driver.findElements(By.className("inventory_item_price"));
            List<Double> pricesUnder20 = priceElements.stream()
                    .map(WebElement::getText)
                    .map(priceText -> priceText.replace("$", "")) // Remove the dollar sign
                    .map(Double::parseDouble) // Convert to Double
                    .filter(price -> price < 20.0) // Filter prices under $20
                    .toList();

            System.out.println("Prices under $20: " + pricesUnder20);
            //Verify that all product names on the page are displayed and are not empty.
            List<WebElement> allNameElements = driver.findElements(By.className("inventory_item_name"));
            boolean allNamesAreNonEmpty = allNameElements.stream()
            .noneMatch(name -> name.getText().isEmpty());

            Assert.assertTrue(allNamesAreNonEmpty, "Some product names are empty!");
            System.out.println("All product names are displayed and non-empty.");

            //Check if the "Test.allTheThings() T-Shirt (Red)" is present on the page.
            List<WebElement> allItemNames = driver.findElements(By.className("inventory_item_name"));
            String searchItem = "Test.allTheThings() T-Shirt (Red)";
            boolean itemFound = allItemNames.stream()
            .anyMatch(e -> e.getText().equals(searchItem));

            Assert.assertTrue(itemFound, "Item not found: " + searchItem);
            System.out.println("Item found: " + searchItem);

            //Get a list of all product names, sorted alphabetically.
            List<WebElement> itemNameElements = driver.findElements(By.className("inventory_item_name"));
            List<String> sortedNames = itemNameElements.stream()
            .map(WebElement::getText)
            .sorted()
            .toList();
            System.out.println("Sorted Product Names: " + sortedNames);
             //Add all items with "Sauce" in the name to the cart.
             List<WebElement> inventoryItems = driver.findElements(By.className("inventory_item"));
             String keyword = "Sauce";
              inventoryItems.stream()
             .filter(e -> e.findElement(By.className("inventory_item_name")).getText().contains(keyword))
             .forEach(e -> e.findElement(By.tagName("button")).click());

             //Alternative approach to prevent stale element reference exception
            

            String itemToSearch = "Sauce";

int itemCount = driver.findElements(By.className("inventory_item")).size();
for (int i = 0; i < itemCount; i++) {
    List<WebElement> currentItems = driver.findElements(By.className("inventory_item"));
    WebElement item = currentItems.get(i);
    String itemName = item.findElement(By.className("inventory_item_name")).getText();
    System.out.println("Checking item: " + itemName);
    if (itemName.contains(itemToSearch)) {
        item.findElement(By.tagName("button")).click();
    }
}
//Find the most expensive item on the page.
            List<WebElement> allPriceElements = driver.findElements(By.className("inventory_item_price"));
            OptionalDouble maxPrice = allPriceElements.stream()
            .map(WebElement::getText)
            .map(priceText -> priceText.replace("$", ""))
            .mapToDouble(Double::parseDouble)
            .max();

            if (maxPrice.isPresent()) {
                System.out.println("Most expensive item price: $" + maxPrice.getAsDouble());
            } else {
                System.out.println("No prices found on the page.");
            }
            
        

            // Get the total price of all items on the page.
            List<WebElement> allProductsPriceElements = driver.findElements(By.className("inventory_item_price"));
            double totalPrice = allProductsPriceElements.stream()
            .map(WebElement::getText)
            .map(priceText -> priceText.replace("$", ""))
            .mapToDouble(Double::parseDouble)
            .sum();

            System.out.println("Total price of all items: $" + totalPrice);
    } finally {
           if (driver != null) {
            driver.quit();
        }
    }
    } 
}      

    
    