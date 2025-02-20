package com.java.userservice.service;

import com.java.userservice.controller.dto.*;
import com.java.userservice.exception.InvalidTokenException;
import com.java.userservice.exception.UsernameExistsException;
import com.java.userservice.mapper.UserMapper;
import com.java.userservice.models.User;
import com.java.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.java.userservice.util.JwtUtil.BEARER_TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public RegisterResponseDTO register(RegisterRequestDTO dto) {
        User user = userMapper.registerRequestDTOToUser(dto);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameExistsException("User already exists with username: " + user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.userToRegisterResponseDTO(savedUser);
    }

    public TokenResponseDTO login(UserRequestDTO dto) {
        User userFromRequest = userMapper.userDTOToUser(dto);
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userFromRequest.getUsername(),
                userFromRequest.getPassword()));
        if (authenticate.isAuthenticated()) {
            Optional<User> userbyUsername = userRepository.findByUsername(userFromRequest.getUsername());
            if (userbyUsername.isPresent()) {
                User user = userbyUsername.get();
                String token = jwtService.generateToken(user.getUsername(), user.getRole());
                return new TokenResponseDTO(token);
            }
        }
        throw new BadCredentialsException("Invalid username or password");
    }

    public void validateToken(String token) {
        if (token.startsWith(BEARER_TOKEN_PREFIX)) {
            token = token.substring(BEARER_TOKEN_PREFIX.length());
        }
        try {
            jwtService.validateToken(token);
        } catch (Exception e) {
            throw new InvalidTokenException("token is invalid");
        }
    }

    public UserResponseDTO getUserDetailsByUsername(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isPresent()) {
            return userMapper.userToUserResponseDTO(byUsername.get());
        }
        throw new UsernameNotFoundException("Unable to find user with username: " + username);
    }
}
