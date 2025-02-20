package com.java.userservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JwtUtil {
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final int TOKEN_LIFETIME = 1000 * 60 * 30;
}
