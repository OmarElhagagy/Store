package com.example.demo.service;

import com.example.demo.entities.CustomerOrder;
import com.example.demo.entities.User;
import com.example.demo.repositories.CustomerOrderRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling order-specific authorization logic
 */
@Service
public class CustomerOrderAuthorizationService {

    private final UserRepository userRepository;
    private final CustomerOrderRepository orderRepository;

    @Autowired
    public CustomerOrderAuthorizationService(UserRepository userRepository, CustomerOrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Determines if the authenticated user is authorized to access the order data
     * 
     * @param authentication The authentication object from Spring Security
     * @param orderId The ID of the order being accessed
     * @return true if authorized, false otherwise
     */
    public boolean isOrderAuthorized(Authentication authentication, Integer orderId) {
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
        
        // If user doesn't have a customer profile, they can't be authorized for the order
        if (user.getCustomer() == null) {
            return false;
        }
        
        // Find the order
        Optional<CustomerOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return false;
        }
        
        CustomerOrder order = orderOptional.get();
        
        // Check if the order belongs to the customer linked to the user
        return order.getCustomer().getId().equals(user.getCustomer().getId());
    }
} 