package com.automation.demo.api.payloads;



import com.automation.demo.ui.utils.LoggerUtil;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;

/**
 * UserPayloads provides methods to construct various request bodies
 * related to user operations, returning type-safe POJOs.
 */
public class UserPayloads {

    private static final Logger logger = LoggerUtil.getLogger(UserPayloads.class);

    @Step("Creating user login request payload")
    public static UserLoginRequest createUserLoginRequest(String email, String password) {
        logger.info("Creating user login request payload");
        return UserLoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    @Step("Creating user registration request payload")
    public static UserRegistrationRequest createUserRegistrationRequest(String name, String job) {
        logger.info("Creating user registration request payload");
        return UserRegistrationRequest.builder()
                .name(name)
                .job(job)
                .build();
    }

    @Step("Creating user update request payload")
    public static UserUpdateRequest createUserUpdateRequest(String name, String job) {
        logger.info("Creating user update request payload");
        return UserUpdateRequest.builder()
                .name(name)
                .job(job)
                .build();
    }
}