package com.automation.demo.api.utils;


import com.automation.demo.ui.utils.LoggerUtil;

import io.qameta.allure.Step;

import io.restassured.response.Response;

import com.automation.demo.api.core.RequestSender;

import org.apache.logging.log4j.Logger;

  /**
     * Sends a GET request to the specified endpoint.
     * @param endpoint The API endpoint path.
     * @return The API Response.
     */
public class RequestBuilderUtil {
    private static final Logger logger = LoggerUtil.getLogger(RequestBuilderUtil.class);


    /**
     * Sends a GET request to the specified endpoint.
     * @param endpoint The API endpoint path.
     * @return The API Response.
     */

    @Step("Sending GET request to {endpoint}")
    public static Response sendGetRequest(RequestSender sender, String endpoint) {
        logger.info("Sending GET request to endpoint: {}", endpoint);
        return sender.send(endpoint, null, "GET");
    }
    /**
     * Sends a POST request with a JSON body to the specified endpoint.
     * @param endpoint The API endpoint path.
     * @param jsonBody The JSON request body as a String or Map.
     * @return The API Response.
     */
    @Step("Sending POST request to {endpoint}")
    public static Response sendPostRequest(RequestSender sender, String endpoint, Object jsonBody) {
        logger.info("Sending POST request to endpoint: {}", endpoint);
        return sender.send(endpoint, jsonBody, "POST");
    }
     /**
     * Sends a DELETE request to the specified endpoint.
     * @param endpoint The API endpoint path.
     * @return The API Response.
     */
    @Step("Sending DELETE request to {endpoint}")
    public static Response sendDeleteRequest(RequestSender sender, String endpoint) {
        logger.info("Sending DELETE request to endpoint: {}", endpoint);
        return sender.send(endpoint, null, "DELETE");
                
    }
    @Step("Sending PUT request to {endpoint}")
    public static Response sendPutRequest(RequestSender sender, String endpoint, Object jsonBody) {
        logger.info("Sending PUT request to endpoint: {}", endpoint);
        return sender.send(endpoint, jsonBody, "PUT");
    }
    /**
     * Sends a PATCH request to the specified endpoint.
     * @param endpoint The API endpoint path.
     * @param jsonBody The JSON request body as a String or Map.
     * @return The API Response.
     */
    @Step("Sending PATCH request to {endpoint}")
    public static Response sendPatchRequest(RequestSender sender, String endpoint, Object jsonBody) {
        logger.info("Sending PATCH request to endpoint: {}", endpoint);
        return sender.send(endpoint, jsonBody, "PATCH");    
    }

}
