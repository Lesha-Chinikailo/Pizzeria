package com.java.userservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageExceptionUtil {
    public static final String UserNotFoundWithUsername = "username not found with name: %s";
    public static final String UserAlreadyExistsWithUsername = "User already exists with username: %s";
    public static final String InvalidUsernameOrPassword = "Invalid username or password";
    public static final String TokenIsInvalid = "token is invalid";
}
