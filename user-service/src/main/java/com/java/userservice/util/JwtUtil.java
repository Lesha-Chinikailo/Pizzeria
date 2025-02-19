package com.java.userservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class JwtUtil {
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final int TOKEN_LIFETIME = 1000 * 60 * 30;
}
