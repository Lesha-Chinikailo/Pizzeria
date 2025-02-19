package com.java.userservice.controller.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenResponseDTO {
    private String token;
}