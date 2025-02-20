package com.java.userservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    private String username;
    private String password;
    private String role;
}