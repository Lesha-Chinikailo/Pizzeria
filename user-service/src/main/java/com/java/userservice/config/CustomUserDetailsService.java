package com.java.userservice.config;

import com.java.userservice.models.User;
import com.java.userservice.repository.UserRepository;
import com.java.userservice.util.MessageExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Configuration
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(MessageExceptionUtil.UserNotFoundWithUsername.formatted(username)));
    }


}