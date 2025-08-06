package com.automation.demo.ui.tests;
import java.io.InputStream;
public class ResourceChecker {
    public static void main(String[] args) {
        String resourcePath = "api/api.properties";
        InputStream inputStream = ResourceChecker.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            System.err.println("Failure: Resource not found: " + resourcePath);
        } else {
            System.out.println("Success! Resource found: " + resourcePath);
        }
    }
}
