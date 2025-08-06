package com.automation.demo.api.core;

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
