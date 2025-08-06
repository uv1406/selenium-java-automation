package com.automation.demo.api.payloads;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO representing the request body for user registration.
 * Uses Lombok annotations to reduce boilerplate code.
 */
@Data // Generates getters, setters, toString, equals, hashCode
@Builder // Provides a fluent builder API for object creation
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
public class UserRegistrationRequest {

    private String name;
    private String job;
    
}
