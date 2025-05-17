package com.example.demo.service;

import com.example.demo.entities.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewService {

    // Create or update a review
    Review saveReview(Review review);
    
    // Get review by ID
    Optional<Review> getReviewById(Integer id);
    
    // Get all reviews for a product
    List<Review> getReviewsByProduct(Integer productId);
    
    // Get all reviews by a customer
    List<Review> getReviewsByCustomer(Integer customerId);
    
    // Get a customer's review for a specific product
    Optional<Review> getCustomerReviewForProduct(Integer customerId, Integer productId);
    
    // Get reviews by rating range
    List<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating);
    
    // Calculate average rating for a product
    Optional<Double> getAverageRatingForProduct(Integer productId);
    
    // Get review count for product
    long getReviewCountForProduct(Integer productId);
    
    // Get recent reviews
    List<Review> getRecentReviews(int limit);
    
    // Search reviews by comment content
    List<Review> searchReviews(String searchTerm);
    
    // Delete a review
    void deleteReview(Integer id);
}