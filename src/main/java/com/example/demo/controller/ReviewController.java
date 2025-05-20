package com.example.demo.controller;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.entities.Customer;
import com.example.demo.entities.Product;
import com.example.demo.entities.Review;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ProductService;
import com.example.demo.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Review Controller", description = "API to manage product reviews")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;
    private final CustomerService customerService;
    private final ProductService productService;

    @Autowired
    public ReviewController(ReviewService reviewService, CustomerService customerService, 
                           ProductService productService) {
        this.reviewService = reviewService;
        this.customerService = customerService;
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all reviews", description = "Returns a list of all reviews")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        logger.info("Fetching all reviews");
        List<Review> reviews = reviewService.getAllReviews();
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID", description = "Returns a single review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review found",
                    content = @Content(schema = @Schema(implementation = ReviewDTO.class))),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<ReviewDTO> getReviewById(
            @Parameter(description = "Review ID", required = true) @PathVariable Integer id) {
        logger.info("Fetching review with ID: {}", id);
        
        Review review = reviewService.findReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        
        return ResponseEntity.ok(ReviewDTO.fromEntity(review));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews by product", description = "Returns reviews for a specific product")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProduct(@PathVariable Integer productId) {
        logger.info("Fetching reviews for product ID: {}", productId);
        
        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        List<Review> reviews = reviewService.getReviewsByProduct(product);
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(reviewDTOs);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or @customerAuthorizationService.isCustomerAuthorized(authentication, #customerId)")
    @Operation(summary = "Get reviews by customer", description = "Returns reviews from a specific customer")
    public ResponseEntity<List<ReviewDTO>> getReviewsByCustomer(@PathVariable Integer customerId) {
        logger.info("Fetching reviews for customer ID: {}", customerId);
        
        Customer customer = customerService.findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        
        List<Review> reviews = reviewService.getReviewsByCustomer(customer);
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(ReviewDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(reviewDTOs);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create new review", description = "Creates a new product review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Product or customer not found")
    })
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        logger.info("Creating new review for product ID: {}", reviewDTO.getProductId());
        
        // Get authenticated user's customer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Customer customer = customerService.findCustomerByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "You must be logged in as a customer to leave a review"));
        
        Product product = productService.findProductById(reviewDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", reviewDTO.getProductId()));
        
        // Check if customer has already reviewed this product
        if (reviewService.hasCustomerReviewedProduct(customer, product)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already reviewed this product");
        }
        
        Review review = reviewService.createReview(
                customer,
                product,
                reviewDTO.getRating(),
                reviewDTO.getComment(),
                LocalDateTime.now()
        );
        
        return new ResponseEntity<>(ReviewDTO.fromEntity(review), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reviewAuthorizationService.isReviewAuthorized(authentication, #id)")
    @Operation(summary = "Update review", description = "Updates an existing review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<ReviewDTO> updateReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody ReviewDTO reviewDTO) {
        logger.info("Updating review with ID: {}", id);
        
        Review existingReview = reviewService.findReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        
        existingReview.setRating(reviewDTO.getRating());
        existingReview.setComment(reviewDTO.getComment());
        existingReview.setReviewDate(LocalDateTime.now());
        
        Review updatedReview = reviewService.updateReview(existingReview);
        return ResponseEntity.ok(ReviewDTO.fromEntity(updatedReview));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @reviewAuthorizationService.isReviewAuthorized(authentication, #id)")
    @Operation(summary = "Delete review", description = "Deletes a review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Review ID", required = true) @PathVariable Integer id) {
        logger.info("Deleting review with ID: {}", id);
        
        Review review = reviewService.findReviewById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product/{productId}/average-rating")
    @Operation(summary = "Get product average rating", description = "Returns the average rating for a product")
    public ResponseEntity<Map<String, Double>> getProductAverageRating(@PathVariable Integer productId) {
        logger.info("Fetching average rating for product ID: {}", productId);
        
        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        Double averageRating = productService.calculateAverageProductRating(productId);
        return ResponseEntity.ok(Map.of("productId", (double) productId, "averageRating", averageRating));
    }
    
    @GetMapping("/helpful")
    @Operation(summary = "Get most helpful reviews", description = "Returns the most helpful reviews based on customer votes")
    public ResponseEntity<List<ReviewDTO>> getMostHelpfulReviews(
            @RequestParam(defaultValue = "10") int limit) {
        logger.info("Fetching {} most helpful reviews", limit);
        
        List<Review> helpfulReviews = reviewService.getMostHelpfulReviews(limit);
        List<ReviewDTO> reviewDTOs = helpfulReviews.stream()
                .map(ReviewDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(reviewDTOs);
    }
} 