package com.example.demo.controller;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.entities.Review;
import com.example.demo.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for managing product reviews
 */
@RestController
@RequestMapping("/api/reviews")
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Get all reviews with pagination
     */
    @GetMapping
    public ResponseEntity<Page<ReviewDTO>> getAllReviews(Pageable pageable) {
        log.info("REST request to get all Reviews with pagination");
        Page<Review> reviews = reviewService.findAllWithPagination(pageable);
        Page<ReviewDTO> reviewDTOs = reviews.map(ReviewDTO::fromEntity);
        return ResponseEntity.ok(reviewDTOs);
    }

    /**
     * Get reviews for a specific product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProduct(@PathVariable Integer productId) {
        log.info("REST request to get Reviews for product ID {}", productId);
        List<Review> reviews = reviewService.findByProductId(productId);
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDTOs);
    }

    /**
     * Get reviews by a specific customer
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#customerId)")
    public ResponseEntity<List<ReviewDTO>> getReviewsByCustomer(@PathVariable Integer customerId) {
        log.info("REST request to get Reviews by customer ID {}", customerId);
        List<Review> reviews = reviewService.findByCustomerId(customerId);
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDTOs);
    }

    /**
     * Get reviews by rating
     */
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByRating(@PathVariable Integer rating) {
        log.info("REST request to get Reviews with rating {}", rating);
        List<Review> reviews = reviewService.findByRating(rating);
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDTOs);
    }

    /**
     * Get a specific review by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Integer id) {
        log.info("REST request to get Review with ID {}", id);
        Optional<Review> review = reviewService.findById(id);
        return review
                .map(value -> ResponseEntity.ok(ReviewDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new review
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#reviewDTO.customerId)")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewDTO reviewDTO, BindingResult bindingResult) {
        log.info("REST request to create Review for product ID {} by customer ID {}", 
                reviewDTO.getProductId(), reviewDTO.getCustomerId());
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            // Validate rating range
            if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
            }
            
            // Check if customer already reviewed this product
            if (reviewService.hasCustomerReviewedProduct(reviewDTO.getCustomerId(), reviewDTO.getProductId())) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Customer has already reviewed this product. Please update the existing review."
                );
            }
            
            Review review = reviewService.createReviewFromDTO(reviewDTO);
            
            return new ResponseEntity<>(ReviewDTO.fromEntity(review), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating review", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating review: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing review
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isReviewResourceOwner(#id)")
    public ResponseEntity<?> updateReview(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewDTO reviewDTO,
            BindingResult bindingResult) {
        log.info("REST request to update Review with ID {}", id);
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            // Validate rating range
            if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
            }
            
            Review existingReview = reviewService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with ID " + id));
            
            // Check that the customer and product IDs match
            if (!existingReview.getCustomer().getId().equals(reviewDTO.getCustomerId()) || 
                !existingReview.getProduct().getId().equals(reviewDTO.getProductId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change review ownership or product");
            }
            
            Review updatedReview = reviewService.updateReviewFromDTO(id, reviewDTO);
            
            return ResponseEntity.ok(ReviewDTO.fromEntity(updatedReview));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating review", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating review: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a review
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isReviewResourceOwner(#id)")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        log.info("REST request to delete Review with ID {}", id);
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting review", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting review: " + e.getMessage(), e);
        }
    }

    /**
     * Approve a review (for moderation)
     * Restricted to admin users
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewDTO> approveReview(@PathVariable Integer id) {
        log.info("REST request to approve Review with ID {}", id);
        try {
            Review review = reviewService.approveReview(id);
            return ResponseEntity.ok(ReviewDTO.fromEntity(review));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error approving review", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error approving review: " + e.getMessage(), e);
        }
    }

    /**
     * Reject a review (for moderation)
     * Restricted to admin users
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewDTO> rejectReview(@PathVariable Integer id, @RequestParam String reason) {
        log.info("REST request to reject Review with ID {}", id);
        try {
            Review review = reviewService.rejectReview(id, reason);
            return ResponseEntity.ok(ReviewDTO.fromEntity(review));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error rejecting review", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error rejecting review: " + e.getMessage(), e);
        }
    }

    /**
     * Handle validation errors and return appropriate response
     */
    private ResponseEntity<Map<String, String>> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
} 