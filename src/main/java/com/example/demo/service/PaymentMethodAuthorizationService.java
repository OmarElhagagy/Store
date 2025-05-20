package com.example.demo.service;

import com.example.demo.entities.PaymentMethod;
import com.example.demo.entities.User;
import com.example.demo.repositories.PaymentMethodRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling payment method-specific authorization logic
 */
@Service
public class PaymentMethodAuthorizationService {

    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    public PaymentMethodAuthorizationService(UserRepository userRepository, PaymentMethodRepository paymentMethodRepository) {
        this.userRepository = userRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    /**
     * Determines if the authenticated user is authorized to access the payment method data
     * 
     * @param authentication The authentication object from Spring Security
     * @param paymentMethodId The ID of the payment method being accessed
     * @return true if authorized, false otherwise
     */
    public boolean isPaymentMethodAuthorized(Authentication authentication, Integer paymentMethodId) {
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
        
        // If user doesn't have a customer profile, they can't be authorized for the payment method
        if (user.getCustomer() == null) {
            return false;
        }
        
        // Find the payment method
        Optional<PaymentMethod> paymentMethodOptional = paymentMethodRepository.findById(paymentMethodId);
        if (paymentMethodOptional.isEmpty()) {
            return false;
        }
        
        PaymentMethod paymentMethod = paymentMethodOptional.get();
        
        // Check if the payment method belongs to the customer linked to the user
        return paymentMethod.getCustomer().getId().equals(user.getCustomer().getId());
    }
} 