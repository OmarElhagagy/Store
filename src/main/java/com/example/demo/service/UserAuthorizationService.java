package com.example.demo.service;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling user-specific authorization logic
 */
@Service
public class UserAuthorizationService {

    private final UserRepository userRepository;

    @Autowired
    public UserAuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Determines if the authenticated user is the same as the requested user ID
     * 
     * @param authentication The authentication object from Spring Security
     * @param userId The ID of the user being accessed
     * @return true if it's the same user, false otherwise
     */
    public boolean isSameUser(Authentication authentication, Integer userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Get the authenticated user's username (email)
        String username = authentication.getName();
        
        // Find the user by username
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        
        // Check if the authenticated user is the same as the requested user ID
        return user.getId().equals(userId);
    }
} 