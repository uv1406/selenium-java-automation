package com.automation.demo.ui.data;

import org.testng.annotations.DataProvider;

public class TestDataProviders {
      @DataProvider(name = "formData")
        public Object[][] getData(){
                   return new Object[][] {
                    {"Ujjawal Verma", "ujjawal@example.com", "123 Test Lane", "456 Automation St"},
            {"John Doe", "john@example.com", "456 A St", "789 B Ave"}
        };}
}
