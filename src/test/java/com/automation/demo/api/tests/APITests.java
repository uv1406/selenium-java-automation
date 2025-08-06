package com.automation.demo.api.tests;

import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.s;

import com.automation.demo.api.base.ApiTestBase;
import com.automation.demo.api.endpoints.ApiEndpoints;

import com.automation.demo.api.payloads.UserRegistrationRequest;
import com.automation.demo.api.payloads.UserUpdateRequest;
import com.automation.demo.api.payloads.UserLoginRequest;
import com.automation.demo.api.payloads.UserPayloads;
import com.automation.demo.api.utils.RequestBuilderUtil;
import com.automation.demo.ui.utils.LoggerUtil;
import com.google.protobuf.Api;

import groovyjarjarantlr4.v4.parse.ANTLRParser.id_return;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.annotations.Test;

 // Keep if you still use Maps for other purposes
import static org.hamcrest.Matchers.*;


@Feature("User Management")
@Epic("API Testing")
public class APITests extends ApiTestBase {
    private static final Logger logger = LoggerUtil.getLogger(APITests.class);
    @Test(priority = 1, description = "Test user registration API endpoint via API")
    @Story("User Registration")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a new user can successfully register via API.")
    public void testUserRegistration() {
        logger.info("Starting user registration test");
        String name= "Test User" + System.currentTimeMillis(); // Unique name for each test run
        String job = "Automation Tester" + System.currentTimeMillis();
        
        UserRegistrationRequest requestBody = UserPayloads.createUserRegistrationRequest(name, job);

            // Now use the constant from ApiEndpoints
        Response response = RequestBuilderUtil.sendPostRequest(ApiTestBase::sendRequestWithRetry, getApiEndpoint(ApiEndpoints.REGISTER_USER), requestBody);

        logger.info("Response received: " + response.asString());
        //Perform all validations using RestAssured's fluent API
        response.then()
                .statusCode(201) // reqres.in returns 200 for successful registration
                .body("name", equalTo(name))
                .body("job", equalTo(job))
                .body("id", notNullValue())
                .body("createdAt", notNullValue());

                 String userId= response.jsonPath().getString("id");
                 ApiTestBase.setUserUpdateId(userId);
                  logger.info("User registered successfully with ID: {}", userId);

        logger.info("User registration test completed successfully for name: {}, job: {}", name, job);
    }

    @Test(priority = 2, description = "Test user update API endpoint via API")
    @Story("User Update")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that an existing user can successfully update their information via API.")
    public void testUserUpdate() {
        logger.info("Starting existing user update test in APITests");
        String updatedName= "Test User" + System.currentTimeMillis(); // Unique name for each test run
        String updatedJob = "Automation Tester" + System.currentTimeMillis();

        // Assuming a user "eve.holt@reqres.in" with password "cityslicka" exists on reqres.in
        String updatedUserId = ApiTestBase.getUserUpdateId();
        if (updatedUserId == null || updatedUserId.isEmpty()) {
            logger.error("No user ID found for update. Please run the registration test first.");
            Assert.fail("No user ID found for update. Please run the registration test first.");
        }
        String updatedEndpoint = getApiEndpoint(ApiEndpoints.UPDATE_USER_PUT) + "/" + updatedUserId;
        UserUpdateRequest requestBody = UserPayloads.createUserUpdateRequest(updatedName, updatedJob);

        Response response = RequestBuilderUtil.sendPutRequest(
            ApiTestBase::sendRequestWithRetry, // Pass the REST sender
            updatedEndpoint, // Get  updated environment-specific endpoint for update
            requestBody
        );

        
        response.then()
                .statusCode(200) // reqres.in returns 200 for successful update
                .body("name", equalTo(updatedName))
                .body("job", equalTo(updatedJob))
                .body("updatedAt", notNullValue());

        // Log the response for debugging
        logger.info("Response received: " + response.asString());
        logger.info("User update test completed successfully for user ID: {}", updatedUserId);
    }

    // You can add more tests here, e.g., for GET, PUT, DELETE, PATCH requests
    // All will use RequestBuilderUtil and pass ApiTestBase::sendRequestWithRetry
}