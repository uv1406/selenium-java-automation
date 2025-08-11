package com.automation.demo.api.base; // Correct package based on your structure


import com.automation.demo.ui.utils.LoggerUtil;


import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import com.fasterxml.jackson.databind.ObjectMapper;

 // Added for per-method auth management (optional)


import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

/**
 * ApiTestBase sets up common configurations for RestAssured,
 * including base URL, request/response logging, and authentication.
 */
public class ApiTestBase {

    private static final Logger logger = LoggerUtil.getLogger(ApiTestBase.class);
    
    // This will be our base specification without authentication headers
    private static RequestSpecification initialBaseRequestSpec; 
    
    // This is the mutable (reassigned) specification used by tests,
    // which may include authentication headers.
    private static ThreadLocal<RequestSpecification> requestSpecThreadLocal = new  ThreadLocal<>(); 


// This ThreadLocal will hold the user update ID for each test method
    // This allows each test method to have its own user update ID without interference.
    private static ThreadLocal<String> userUpdateIdThreadLocal = new  ThreadLocal<>(); 
    
    protected static String authToken; // To store authentication token
    private static String apiKey;

    // Properties object to load API endpoint URLs from properties file
    // This will be initialized in the setupApiBaseSuite method
    private static Properties apiProperties;
    private static String currentEnvironment; // To store the current environment by Maven profile
    private static final int DEFAULT_MAX_RETRIES = 3; // Default retry count if not specified in properties
    private static final long DEFAULT_RETRY_DELAY_MILLIS = 1000L; // Default retry delay in milliseconds

    /**
     * This method is called before the test suite starts to set up the base RequestSpecification.
     * It loads API endpoints from a properties file and initializes the base RequestSpecification.
     */

    @BeforeSuite(alwaysRun = true) // Ensures this runs once before any API tests in the suite
    public void setupApiBaseSuite() {
        logger.info("Setting up API base configurations...");

       currentEnvironment = System.getProperty("env"); // Get environment from Maven profile or default to "default"
       if (currentEnvironment ==null || currentEnvironment.trim().isEmpty()) {
            currentEnvironment = "default"; // Default environment if not specified
            logger.warn("No environment specified. Using default: qa " + currentEnvironment);
            currentEnvironment = "qa"; // Default to QA if no environment is specified
        } 
        currentEnvironment = currentEnvironment.trim().toLowerCase();
        logger.info("Current environment set to: " + currentEnvironment);

        // Load API endpoints from api.properties
        apiProperties = new Properties();

       String apiPropertiesPath = "api/api.properties"; // Path relative to classpath
       try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(apiPropertiesPath)) {
        if (inputStream == null) {
            logger.error("Configuration file '" + apiPropertiesPath + "' not found in the classpath.");
            throw new RuntimeException("Configuration file '" + apiPropertiesPath + "' not found.");
        }
        apiProperties.load(inputStream);
        logger.info("Successfully loaded API properties from: " + apiPropertiesPath);
    } catch (IOException e) {
        logger.error("Failed to load API properties from " + apiPropertiesPath + ": " + e.getMessage(), e);
        throw new RuntimeException("Could not load API properties.", e);
    }
        String apiKeyKey = "api.key." + currentEnvironment;
        apiKey = apiProperties.getProperty(apiKeyKey);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.error("API key '{}' not found in api.properties for environment '{}'.", apiKeyKey, currentEnvironment);
            throw new RuntimeException("API key is not configured for environment: " + currentEnvironment);
        }
        // Get base URL from api.properties
        String baseUrlKey = "api.base.url." + currentEnvironment;
        String baseUrl = apiProperties.getProperty(baseUrlKey);
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            logger.error("API base URL '{}' not found in api.properties for environment '{}'.", baseUrlKey, currentEnvironment);
            throw new RuntimeException("API base URL is not configured for environment: " + currentEnvironment);
        }
         // Build and set the global RestAssured configuration
        RestAssuredConfig config = RestAssuredConfig.config()
            .objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                (type, s) -> new ObjectMapper()
            ));

        RestAssured.config = config;
        logger.info("RestAssured config object set.");
    // Build the initial base RequestSpecification (without auth)
    RequestSpecBuilder specBuilder = new RequestSpecBuilder()
            .setBaseUri(baseUrl)
            .setContentType(ContentType.JSON) // Default content type for requests
            .addFilter(new RequestLoggingFilter())  // Log requests to console/logs
            .addFilter(new ResponseLoggingFilter()); // Log responses to console/logs

    if (apiKey != null && !apiKey.trim().isEmpty()) {
        specBuilder.addHeader("x-api-key", apiKey);
        logger.info("API key for environment '" + currentEnvironment + "' is set.");
    } else {
        logger.warn("No API key found for environment '" + currentEnvironment + "'.");
    }

    initialBaseRequestSpec = specBuilder.build();

  // RestAssured.requestSpecification = initialBaseRequestSpec;
    
    logger.info("Global RequestSpecification initialized and set. Object hash: " + initialBaseRequestSpec.hashCode());
    
    logger.info("API base setup complete.");

    }
    @BeforeMethod(alwaysRun = true) // Ensures this runs before each API test method
    public void setUpApiBaseMethod() {
        // This method can be used to reset or reinitialize the base RequestSpecification
        // if needed for each test method, but typically setupApiBaseSuite is sufficient.
        logger.info("Setting up API base for test method...");
         // Ensure the initialBaseRequestSpec is not null before setting it
    if (initialBaseRequestSpec == null) {
        logger.error("initialBaseRequestSpec is null. The @BeforeSuite may have failed or not run. This is the root cause of the issue.");
        throw new IllegalStateException("API base setup failed. initialBaseRequestSpec is null.");
    }
        // Reset requestSpec to the initial base specification
        requestSpecThreadLocal.set(initialBaseRequestSpec);
        logger.info("API base RequestSpecification reset for test method.");
    }
    /**
     * This method is called after each test method to clean up the RequestSpecification.
     * It can be used to reset the RequestSpecification or clear any authentication tokens.
     */
    @AfterMethod(alwaysRun = true) // Ensures this runs after each API test method
    public void tearDownApiBaseMethod() {
        // This method can be used to clean up or reset the RequestSpecification after each test
        logger.info("Tearing down API base for test method...");
        
        // Clear the ThreadLocal to avoid memory leaks
        requestSpecThreadLocal.remove();
        authToken = null; // Clear auth token if needed
        logger.info("API base RequestSpecification cleared for test method.");
    }   

    public static RequestSpecification getRequestSpec() {
        // Return the current RequestSpecification for tests to use
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


@AfterSuite(alwaysRun = true)
public void tearDownApiBaseSuite() {
    logger.info("Tearing down API base suite...");
    initialBaseRequestSpec = null; // Correct to do this ONLY at the end of the suite
    logger.info("API base suite tear down complete.");
}

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
     * Sets the authentication token for subsequent requests by creating a NEW RequestSpecification
     * based on the base one, adding the Authorization header.
     * @param token The authentication token (e.g., JWT).
     */
    protected void setAuthToken(String token) {
        authToken = token;
        // Create a NEW RequestSpecification based on the base one, with the Authorization header added
        requestSpecThreadLocal.set(getRequestSpec().header("Authorization", "Bearer " + token));
        logger.info("Authentication token set for API requests.");
    }

    /**
     * Clears the authentication token by reverting requestSpec back to the base specification
     * (which does not contain the Authorization header).
     */
    protected void clearAuthToken() {
        requestSpecThreadLocal.set(initialBaseRequestSpec);
        authToken = null; // Clear the auth token   
        logger.info("Authentication token cleared for API requests.");
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
                // Get the thread's current spec (which should be initialBaseRequestSpec or one with auth)
                RequestSpecification currentSpec = getRequestSpec(); // Get the thread's current spec
                 // CRUCIAL: Start with RestAssured.given() to ensure global config is applied,
                // then apply the currentSpec (from ThreadLocal)
                RequestSpecification finalSpec = RestAssured.given().spec(currentSpec);

                // Build the request based on HTTP method
                if ("POST".equalsIgnoreCase(httpMethod)) {
                    response = finalSpec.body(requestBody).post(endpoint);
                } else if ("GET".equalsIgnoreCase(httpMethod)) {
                    response = finalSpec.get(endpoint);
                } else if ("PUT".equalsIgnoreCase(httpMethod)) {
                    response = finalSpec.body(requestBody).put(endpoint);
                } else if ("DELETE".equalsIgnoreCase(httpMethod)) {
                    response = finalSpec.delete(endpoint);
                } else if ("PATCH".equalsIgnoreCase(httpMethod)) {
                    response = finalSpec.body(requestBody).patch(endpoint);
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
    /**
     * Sets the user update ID for the current test method.
     * This allows each test method to have its own user update ID without interference.
     *
     * @param userId The user ID to set for the current test method.
     */
    public static void setUserUpdateId(String userId) {
        userUpdateIdThreadLocal.set(userId);
    }

    public static String getUserId() {
        return userUpdateIdThreadLocal.get();
    }

    public static void clearUserId() {
        userUpdateIdThreadLocal.remove();
    }
}