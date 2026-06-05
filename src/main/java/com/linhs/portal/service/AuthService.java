package com.linhs.portal.service;

import com.linhs.portal.model.User;
import com.linhs.portal.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Bridge method to match PageController expectations.
     */
    public Optional<User> authenticate(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }

        String normalizedEmail = normalizeEmail(username);
        Optional<User> userOptional = userRepository.findByEmail(normalizedEmail);
        if (userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {
            return userOptional;
        }

        return Optional.empty();
    }

    public boolean login(String email, String password, HttpSession session) {
        if (email == null || password == null || session == null) {
            return false;
        }

        String normalizedEmail = normalizeEmail(email);
        Optional<User> userOptional = userRepository.findByEmail(normalizedEmail);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return false;
        }

        session.setAttribute("user", user);
        session.setAttribute("email", user.getEmail());
        session.setAttribute("role", user.getRoleName());

        return true;
    }

    private String normalizeEmail(String input) {
        String cleaned = input.trim().toLowerCase();
        if (!cleaned.contains("@")) {
            cleaned = cleaned + "@linhs.edu.ph";
        }
        return cleaned;
    }

    public User registerUser(User user) {
        if (user == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User details, email, and password fields cannot be empty.");
        }

        String normalizedEmail = user.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            return null;
        }

        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRoleName() != null) {
            user.setRoleName(user.getRoleName().toUpperCase());
        }

        return userRepository.save(user);
    }

    /**
     * Encodes a plain text password using the configured password encoder.
     * Used for password encoding in registration and user management.
     */
    public String encodePassword(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return passwordEncoder.encode(rawPassword);
    }
}