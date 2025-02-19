package com.java.userservice.controller.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;
    private String password;
    private String role;
}
