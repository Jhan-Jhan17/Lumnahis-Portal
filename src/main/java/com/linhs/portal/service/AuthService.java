package com.linhs.portal.service;

import com.linhs.portal.model.User;
import com.linhs.portal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Inject both UserRepository and PasswordEncoder via constructor
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Check if the raw password matches the encoded password in the database
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null; // Authentication failed
    }
}