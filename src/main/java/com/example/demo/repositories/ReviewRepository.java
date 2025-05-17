package com.example.demo.repositories;

import com.example.demo.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // Find reviews by product
    List<Review> findByProductId(Integer productId);
    
    // Find reviews by customer
    List<Review> findByCustomerId(Integer customerId);
    
    // Find review by product and customer
    Optional<Review> findByProductIdAndCustomerId(Integer productId, Integer customerId);
    
    // Find reviews by rating range
    List<Review> findByRatingBetween(Integer minRating, Integer maxRating);
    
    // Calculate average rating for product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Optional<Double> calculateAverageRating(@Param("productId") Integer productId);
    
    // Count reviews by product
    long countByProductId(Integer productId);
    
    // Find recent reviews
    List<Review> findTop5ByOrderByReviewDateDesc();
    
    // Search reviews by comment content
    @Query("SELECT r FROM Review r WHERE LOWER(r.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Review> searchByComment(@Param("searchTerm") String searchTerm);
}