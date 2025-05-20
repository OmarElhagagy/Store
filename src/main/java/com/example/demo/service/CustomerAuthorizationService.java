package com.example.demo.service;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling customer-specific authorization logic
 */
@Service
public class CustomerAuthorizationService {

    private final UserRepository userRepository;

    @Autowired
    public CustomerAuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Determines if the authenticated user is authorized to access the customer data
     * 
     * @param authentication The authentication object from Spring Security
     * @param customerId The ID of the customer being accessed
     * @return true if authorized, false otherwise
     */
    public boolean isCustomerAuthorized(Authentication authentication, Integer customerId) {
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
        
        // Check if the user is linked to the customer being accessed
        if (user.getCustomer() != null && user.getCustomer().getId().equals(customerId)) {
            return true;
        }
        
        return false;
    }
} 