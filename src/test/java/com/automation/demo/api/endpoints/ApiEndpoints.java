package com.automation.demo.api.endpoints;

/**
 * ApiEndpoints provides logical keys for all API endpoint paths.
 * This centralizes endpoint definitions and provides compile-time safety
 * when referencing keys from the api.properties file.
 */
public class ApiEndpoints {
    
    // --- Authentication Endpoints ---
    public static final String REGISTER_USER = "api.users.register";
    public static final String LOGIN_USER = "api.users.login";
    public static final String REFRESH_TOKEN = "api.users.refresh-token"; 

    // --- User Management Endpoints ---
    // Note: These keys match the structure in your api.properties file
    public static final String GET_USER_PROFILE = "api.users.profile";
    public static final String DELETE_USER = "api.users.delete";
    public static final String UPDATE_USER_PUT = "api.users.update.put";
    public static final String UPDATE_USER_PATCH = "api.users.update.patch";

    // Add more endpoint constants as your API grows
    // public static final String GET_PRODUCTS = "api.products.get-all";
    // public static final String CREATE_ORDER = "api.orders.create";

    // Private constructor to prevent instantiation
    private ApiEndpoints() {
        // Utility class
    }
}