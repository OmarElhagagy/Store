package com.example.demo.service;

import com.example.demo.entities.Address;
import com.example.demo.entities.User;
import com.example.demo.repositories.AddressRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling address-specific authorization logic
 */
@Service
public class AddressAuthorizationService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public AddressAuthorizationService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    /**
     * Determines if the authenticated user is authorized to access the address data
     * 
     * @param authentication The authentication object from Spring Security
     * @param addressId The ID of the address being accessed
     * @return true if authorized, false otherwise
     */
    public boolean isAddressAuthorized(Authentication authentication, Integer addressId) {
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
        
        // If user doesn't have a customer profile, they can't be authorized for the address
        if (user.getCustomer() == null) {
            return false;
        }
        
        // Find the address
        Optional<Address> addressOptional = addressRepository.findById(addressId);
        if (addressOptional.isEmpty()) {
            return false;
        }
        
        Address address = addressOptional.get();
        
        // Check if the address belongs to the customer linked to the user
        return address.getCustomer().getId().equals(user.getCustomer().getId());
    }
} 