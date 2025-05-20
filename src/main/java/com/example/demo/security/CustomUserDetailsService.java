package com.example.demo.security;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // In this application, roles are stored as a string in the role field
        // Create authority based on the user's role
        Set<GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()));

        // Check if user is locked
        boolean isAccountNonLocked = user.getLockedUntil() == null || 
                                    Instant.now().isAfter(user.getLockedUntil());
        
        // We don't have a specific active flag on User, assume true if user exists
        boolean isEnabled = true;
        
        // If user has a customer, check if it's active
        // This approach assumes Customer has an isActive() method, based on the earlier grep search
        if (user.getCustomer() != null) {
            try {
                // Use direct method call instead of reflection
                isEnabled = user.getCustomer().isActive();
            } catch (Exception e) {
                // Fall back to enabled if there's an exception
                isEnabled = true;
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(), // User entity uses passwordHash field
                isEnabled,              // Enabled status 
                true,                   // Account non-expired (always true)
                true,                   // Credentials non-expired (always true)
                isAccountNonLocked,     // Account non-locked
                authorities             // Authorities from single role
        );
    }
} 