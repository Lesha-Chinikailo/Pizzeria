package com.java.userservice.controller;

import com.java.userservice.controller.dto.*;
import com.java.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> registerUser(@RequestBody RegisterRequestDTO user) {
        RegisterResponseDTO register = userService.register(user);
        return ResponseEntity.ok(register);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody UserRequestDTO userRequestDTO) {
        TokenResponseDTO token = userService.login(userRequestDTO);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        userService.validateToken(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserDetailsByUsername(username));
    }
}
