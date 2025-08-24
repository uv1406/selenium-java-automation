package com.automation.demo.core;

import io.restassured.response.Response;

/**
 * Functional interface defining the contract for sending an HTTP request.
 * This abstracts away the underlying client (RestAssured, GraphQL client, etc.)
 * and retry logic.
 */
@FunctionalInterface
public interface RequestSender {
    /**
     * Sends an HTTP request.
     * @param endpoint The specific path or identifier for the API resource.
     * @param requestBody The request body (POJO, String, Map, or null if no body).
     * @param httpMethod The HTTP method (e.g., "GET", "POST", "PUT", "DELETE", "PATCH").
     * @return The API Response.
     */
    Response send(String endpoint, Object requestBody, String httpMethod);
}

package com.automation.demo.api.base;

import com.automation.demo.core.RequestSender; // Import the new interface
import com.automation.demo.utils.LoggerUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static io.restassured.RestAssured.given; // Import static given() for direct use

/**
 * ApiTestBase sets up common configurations for RestAssured,
 * including base URL, request/response logging, and manages authentication tokens
 * in a thread-safe manner for parallel execution. It also handles
 * environment-specific endpoint resolution using system properties (set by Maven profiles).
 * This class also provides the concrete implementation of RequestSender for REST APIs.
 */
public class ApiTestBase {

    private static final Logger logger = LoggerUtil.getLogger(ApiTestBase.class);

    private static ThreadLocal<RequestSpecification> requestSpecThreadLocal = new ThreadLocal<>();
    private static RequestSpecification initialBaseRequestSpec;
    private static Properties apiProperties;

    public static String currentEnvironment; // Set by Maven profile

    // Default retry parameters for the core API request sender
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_RETRY_DELAY_MILLIS = 1000L;

    @BeforeSuite(alwaysRun = true)
    public void setupApiBaseSuite() {
        logger.info("Setting up API base configurations for the suite...");

        currentEnvironment = System.getProperty("env");
        if (currentEnvironment == null || currentEnvironment.trim().isEmpty()) {
            logger.warn("Environment system property 'env' not set. Defaulting to 'qa'.");
            currentEnvironment = "qa";
        }
        currentEnvironment = currentEnvironment.toLowerCase();
        logger.info("Running tests in '{}' environment.", currentEnvironment);

        apiProperties = new Properties();
        String apiPropertiesPath = "api.properties";
        try (FileInputStream fis = new FileInputStream(
                getClass().getClassLoader().getResource(apiPropertiesPath).getFile())) {
            apiProperties.load(fis);
            logger.info("Successfully loaded API properties from: " + apiPropertiesPath);
        } catch (IOException | NullPointerException e) {
            logger.error("Failed to load API properties from " + apiPropertiesPath + ": " + e.getMessage(), e);
            throw new RuntimeException("Could not load API properties file.", e);
        }

        String baseUrlKey = "api.base.url." + currentEnvironment;
        String baseUrl = apiProperties.getProperty(baseUrlKey);
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            logger.error("API base URL '{}' not found in api.properties for environment '{}'.", baseUrlKey, currentEnvironment);
            throw new RuntimeException("API base URL is not configured for environment: " + currentEnvironment);
        }

        initialBaseRequestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();

        logger.info("RestAssured base URI set to: " + baseUrl);
        logger.info("API base suite setup complete.");
    }

    @BeforeMethod(alwaysRun = true)
    public void setupApiBaseMethod() {
        requestSpecThreadLocal.set(initialBaseRequestSpec);
        logger.debug("RequestSpecification initialized for current thread.");
    }

    @AfterMethod(alwaysRun = true)
    public void teardownApiBaseMethod() {
        requestSpecThreadLocal.remove();
        logger.debug("RequestSpecification removed from ThreadLocal for current thread.");
    }

    /**
     * Retrieves the RequestSpecification for the current thread.
     * This is used internally by the framework to build requests.
     *
     * @return The RequestSpecification object for the current thread.
     */
    public static RequestSpecification getRequestSpec() {
        return requestSpecThreadLocal.get();
    }

    /**
     * Retrieves an API endpoint path from api.properties,
     * dynamically selecting the correct path based on the current environment.
     *
     * @param logicalEndpointKey The logical key for the endpoint (e.g., "api.users.login").
     * @return The actual endpoint path string for the current environment.
     * @throws RuntimeException if the endpoint is not found for the given key and environment.
     */
    public static String getApiEndpoint(String logicalEndpointKey) {
        String envSpecificKey = logicalEndpointKey + "." + currentEnvironment;
        String endpoint = apiProperties.getProperty(envSpecificKey);

        if (endpoint == null || endpoint.trim().isEmpty()) {
            endpoint = apiProperties.getProperty(logicalEndpointKey);
            if (endpoint != null && !endpoint.trim().isEmpty()) {
                logger.warn("No environment-specific endpoint found for '{}' in '{}' environment. Using generic endpoint: '{}'",
                        logicalEndpointKey, currentEnvironment, endpoint);
            }
        }

        if (endpoint == null || endpoint.trim().isEmpty()) {
            logger.error("API endpoint '{}' not found in api.properties for environment '{}'. " +
                         "Also no generic key '{}' found.", envSpecificKey, currentEnvironment, logicalEndpointKey);
            throw new RuntimeException("API endpoint '" + logicalEndpointKey + "' is not configured for environment: " + currentEnvironment);
        }
        return endpoint;
    }

    /**
     * Sets the authentication token for subsequent requests made by the current thread.
     * It does this by creating a NEW RequestSpecification derived from the current one,
     * adding the Authorization header, and then updating the thread-local spec.
     *
     * @param token The authentication token (e.g., JWT).
     */
    protected void setAuthToken(String token) {
        requestSpecThreadLocal.set(getRequestSpec().header("Authorization", "Bearer " + token));
        logger.info("Authentication token set for API requests in current thread.");
    }

    /**
     * Clears any previously set authentication token for the current thread
     * by reverting its RequestSpecification back to the initial base specification.
     */
    protected void clearAuthToken() {
        requestSpecThreadLocal.set(initialBaseRequestSpec);
        logger.info("Authentication token cleared for API requests in current thread.");
    }

    /**
     * The core engine for sending REST API requests with retry logic.
     * This method implements the RequestSender contract for REST APIs.
     * It uses the thread-local RequestSpecification internally.
     *
     * @param endpoint The API endpoint path.
     * @param requestBody The request body object (POJO, Map, String, or null for GET/DELETE).
     * @param httpMethod The HTTP method (GET, POST, PUT, DELETE, PATCH).
     * @return The API Response.
     * @throws RuntimeException if the request fails after all retries or an unsupported method is provided.
     */
    public static Response sendRequestWithRetry(String endpoint, Object requestBody, String httpMethod) {
        // This method is the concrete implementation of RequestSender's 'send' method.
        // It provides the retry logic and uses the thread-local RequestSpecification.
        Response response = null;
        for (int i = 0; i <= DEFAULT_MAX_RETRIES; i++) {
            try {
                RequestSpecification currentSpec = getRequestSpec(); // Get the thread's current spec

                // Build the request based on HTTP method
                if ("POST".equalsIgnoreCase(httpMethod)) {
                    response = currentSpec.body(requestBody).post(endpoint);
                } else if ("GET".equalsIgnoreCase(httpMethod)) {
                    response = currentSpec.get(endpoint);
                } else if ("PUT".equalsIgnoreCase(httpMethod)) {
                    response = currentSpec.body(requestBody).put(endpoint);
                } else if ("DELETE".equalsIgnoreCase(httpMethod)) {
                    response = currentSpec.delete(endpoint);
                } else if ("PATCH".equalsIgnoreCase(httpMethod)) {
                    response = currentSpec.body(requestBody).patch(endpoint);
                } else {
                    throw new IllegalArgumentException("Unsupported HTTP method for retry: " + httpMethod);
                }

                if (response.statusCode() < 500) { // Success (2xx) or client error (4xx) - no retry needed
                    logger.debug("Request successful or client error (Status: {}). No retry needed.", response.statusCode());
                    return response;
                }
                logger.warn("Received 5xx error (Status: {}), retrying... Attempt {}/{}", response.statusCode(), (i + 1), DEFAULT_MAX_RETRIES);

                if (i < DEFAULT_MAX_RETRIES) {
                    Thread.sleep(DEFAULT_RETRY_DELAY_MILLIS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Request retry interrupted: " + e.getMessage());
                throw new RuntimeException("API request retry interrupted.", e);
            } catch (Exception e) {
                logger.error("Error during API request attempt " + (i + 1) + ": " + e.getMessage(), e);
                if (i == DEFAULT_MAX_RETRIES) {
                    throw new RuntimeException("API request failed after " + (DEFAULT_MAX_RETRIES + 1) + " attempts for endpoint: " + endpoint, e);
                }
            }
        }
        throw new RuntimeException("API request failed after " + (DEFAULT_MAX_RETRIES + 1) + " attempts for endpoint: " + endpoint);
    }
}

package com.automation.demo.api.utils;

import com.automation.demo.core.RequestSender; // Import the RequestSender interface
import com.automation.demo.utils.LoggerUtil;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.apache.logging.log4j.Logger;

/**
 * RequestBuilderUtil provides high-level, convenient methods for test writers
 * to send various HTTP requests. It uses a provided RequestSender implementation
 * to perform the actual request sending and retry logic, abstracting away
 * the underlying API client and framework details.
 */
public class RequestBuilderUtil {
    private static final Logger logger = LoggerUtil.getLogger(RequestBuilderUtil.class);

    /**
     * Sends a GET request to the specified endpoint using the provided sender.
     * @param sender The RequestSender instance (e.g., ApiTestBase::sendRequestWithRetry).
     * @param endpoint The API endpoint path.
     * @return The API Response.
     */
    @Step("Sending GET request to {endpoint}")
    public static Response sendGetRequest(RequestSender sender, String endpoint) {
        logger.info("Sending GET request to endpoint: {}", endpoint);
        // Delegate to the provided sender
        return sender.send(endpoint, null, "GET");
    }

    /**
     * Sends a POST request with a JSON body to the specified endpoint using the provided sender.
     * @param sender The RequestSender instance (e.g., ApiTestBase::sendRequestWithRetry).
     * @param endpoint The API endpoint path.
     * @param jsonBody The JSON request body as an Object (POJO, Map, String).
     * @return The API Response.
     */
    @Step("Sending POST request to {endpoint}")
    public static Response sendPostRequest(RequestSender sender, String endpoint, Object jsonBody) {
        logger.info("Sending POST request to endpoint: {}", endpoint);
        // Delegate to the provided sender
        return sender.send(endpoint, jsonBody, "POST");
    }

    /**
     * Sends a DELETE request to the specified endpoint using the provided sender.
     * @param sender The RequestSender instance (e.g., ApiTestBase::sendRequestWithRetry).
     * @param endpoint The API endpoint path.
     * @return The API Response.
     */
    @Step("Sending DELETE request to {endpoint}")
    public static Response sendDeleteRequest(RequestSender sender, String endpoint) {
        logger.info("Sending DELETE request to endpoint: {}", endpoint);
        // Delegate to the provided sender
        return sender.send(endpoint, null, "DELETE");
    }

    /**
     * Sends a PUT request with a JSON body to the specified endpoint using the provided sender.
     * @param sender The RequestSender instance (e.g., ApiTestBase::sendRequestWithRetry).
     * @param endpoint The API endpoint path.
     * @param jsonBody The JSON request body as an Object (POJO, Map, String).
     * @return The API Response.
     */
    @Step("Sending PUT request to {endpoint}")
    public static Response sendPutRequest(RequestSender sender, String endpoint, Object jsonBody) {
        logger.info("Sending PUT request to endpoint: {}", endpoint);
        // Delegate to the provided sender
        return sender.send(endpoint, jsonBody, "PUT");
    }

    /**
     * Sends a PATCH request with a JSON body to the specified endpoint using the provided sender.
     * @param sender The RequestSender instance (e.g., ApiTestBase::sendRequestWithRetry).
     * @param endpoint The API endpoint path.
     * @param jsonBody The JSON request body as an Object (POJO, Map, String).
     * @return The API Response.
     */
    @Step("Sending PATCH request to {endpoint}")
    public static Response sendPatchRequest(RequestSender sender, String endpoint, Object jsonBody) {
        logger.info("Sending PATCH request to endpoint: {}", endpoint);
        // Delegate to the provided sender
        return sender.send(endpoint, jsonBody, "PATCH");
    }
}

package com.automation.demo.api.tests;

import com.automation.demo.api.base.ApiTestBase; // Import ApiTestBase
import com.automation.demo.api.endpoints.ApiEndpoints; // Import ApiEndpoints
import com.automation.demo.api.payload.UserLoginRequest; // Import UserLoginRequest
import com.automation.demo.api.payload.UserPayloads; // Import UserPayloads
import com.automation.demo.api.utils.RequestBuilderUtil; // Import RequestBuilderUtil
import com.automation.demo.utils.LoggerUtil; // Import LoggerUtil
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.apache.logging.log4j.Logger; // Correct Logger import for Log4j2

import static org.hamcrest.Matchers.*; // For Hamcrest matchers

@Feature("User Management") // Use Feature annotation for test suite grouping
public class APITests extends ApiTestBase { // Test class extends ApiTestBase

    // Correct Logger import and initialization for Log4j2
    private static final Logger logger = LoggerUtil.getLogger(APITests.class);

    @Test(priority = 1, description = "Test user registration API endpoint")
    @Story("User Registration")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a new user can successfully register via API.")
    public void testUserRegistration() {
        logger.info("Starting user registration test in {} environment.", currentEnvironment);

        String email = "eva.holt_" + System.currentTimeMillis() + "@reqres.in"; // Make email unique
        String password = "pistol";
        UserLoginRequest requestBody = UserPayloads.createUserLoginRequest(email, password); // Use the POJO

        // Call RequestBuilderUtil, passing ApiTestBase.sendRequestWithRetry as the sender
        Response response = RequestBuilderUtil.sendPostRequest(
            ApiTestBase::sendRequestWithRetry, // Method reference for the RequestSender interface
            getApiEndpoint(ApiEndpoints.REGISTER_USER), // Get environment-specific endpoint
            requestBody // The POJO request body
        );

        // Validate response
        Assert.assertEquals(response.getStatusCode(), 200, "User registration failed - Status Code Mismatch"); // reqres.in returns 200 for successful registration
        response.then()
                .body("id", notNullValue())
                .body("token", notNullValue());
        logger.info("User registration test completed successfully for email: {}", email);
    }
    
    @Test(priority = 2, description = "Test user login API endpoint")
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that an existing user can successfully log in via API.")
    public void testUserLogin() {
        logger.info("Starting user login test in {} environment.", currentEnvironment);

        // Assuming a user "eve.holt@reqres.in" with password "cityslicka" exists on reqres.in
        String email = "eve.holt@reqres.in";
        String password = "cityslicka";
        UserLoginRequest requestBody = UserPayloads.createUserLoginRequest(email, password);

        Response response = RequestBuilderUtil.sendPostRequest(
            ApiTestBase::sendRequestWithRetry, // Pass the REST sender
            getApiEndpoint(ApiEndpoints.LOGIN_USER), // Get environment-specific endpoint
            requestBody
        );

        Assert.assertEquals(response.getStatusCode(), 200, "User login failed - Status Code Mismatch");
        response.then()
                .body("token", notNullValue());
        logger.info("User login test completed successfully for email: {}", email);
    }

    // You can add more tests here, e.g., for GET, PUT, DELETE, PATCH requests
    // All will use RequestBuilderUtil and pass ApiTestBase::sendRequestWithRetry
}