package com.example.demo.dto;

import com.example.demo.entities.Review;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Data Transfer Object for Review entities
 * Used for API requests and responses related to product reviews
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDTO {

    Integer id;
    
    @NotNull(message = "Product ID is required")
    Integer productId;
    
    String productName;
    
    @NotNull(message = "Customer ID is required")
    Integer customerId;
    
    String customerName;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    Integer rating;
    
    String comment;
    
    Instant reviewDate;
    
    /**
     * Converts a Review entity to ReviewDTO
     *
     * @param review The Review entity
     * @return ReviewDTO
     */
    public static ReviewDTO fromEntity(Review review) {
        if (review == null) {
            return null;
        }
        
        String customerName = null;
        if (review.getCustomer() != null) {
            String firstName = review.getCustomer().getFName();
            String lastName = review.getCustomer().getLName();
            customerName = firstName + " " + lastName;
        }
        
        return ReviewDTO.builder()
                .id(review.getId())
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .productName(review.getProduct() != null ? review.getProduct().getProductName() : null)
                .customerId(review.getCustomer() != null ? review.getCustomer().getId() : null)
                .customerName(customerName)
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getReviewDate())
                .build();
    }
    
    /**
     * Updates an existing Review entity with DTO data
     * Note: This doesn't set the associations which should be handled separately
     *
     * @param entity The Review entity to update
     * @return Updated Review entity
     */
    public Review updateEntity(Review entity) {
        entity.setRating(this.rating);
        entity.setComment(this.comment);
        if (this.reviewDate != null) {
            entity.setReviewDate(this.reviewDate);
        }
        return entity;
    }
}