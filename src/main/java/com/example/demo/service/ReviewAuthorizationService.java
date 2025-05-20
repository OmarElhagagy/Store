package com.example.demo.service;

import com.example.demo.entities.Review;
import com.example.demo.entities.User;
import com.example.demo.repositories.ReviewRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling review-specific authorization logic
 */
@Service
public class ReviewAuthorizationService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewAuthorizationService(UserRepository userRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Determines if the authenticated user is authorized to access/modify the review data
     * 
     * @param authentication The authentication object from Spring Security
     * @param reviewId The ID of the review being accessed
     * @return true if authorized, false otherwise
     */
    public boolean isReviewAuthorized(Authentication authentication, Integer reviewId) {
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
        
        // If user doesn't have a customer profile, they can't be authorized for the review
        if (user.getCustomer() == null) {
            return false;
        }
        
        // Find the review
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (reviewOptional.isEmpty()) {
            return false;
        }
        
        Review review = reviewOptional.get();
        
        // Check if the review belongs to the customer linked to the user
        return review.getCustomer().getId().equals(user.getCustomer().getId());
    }
} 