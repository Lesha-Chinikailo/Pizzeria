package com.java.productservice.service;

import com.java.productservice.client.AuthServiceClient;
import com.java.productservice.controller.dto.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ResponseEntity<UserResponseDTO> userByUsername = authServiceClient.getUserByUsername(username);
        UserResponseDTO userResponseDTO = userByUsername.getBody();
        userResponseDTO.setPassword(passwordEncoder.encode(userResponseDTO.getPassword()));
        return new User(userResponseDTO.getUsername(), userResponseDTO.getPassword(), getAuthorities(userResponseDTO.getRole()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role));
    }
}