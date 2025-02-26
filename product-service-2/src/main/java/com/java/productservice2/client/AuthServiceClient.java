package com.java.productservice2.client;

import com.java.productservice2.controller.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", url = "${auth-service.url}")
public interface AuthServiceClient {
    @GetMapping("/user/{username}")
    ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username);
}
