package com.linhs.portal.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Deactivated to allow your manual PageController session logic to manage logins safely
        throw new UsernameNotFoundException("Spring Security UserDetailsService is disabled. Use AuthService instead.");
    }
}