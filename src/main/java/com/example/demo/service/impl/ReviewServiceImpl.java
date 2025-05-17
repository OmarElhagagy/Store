package com.example.demo.service.impl;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional
    public Review saveReview(Review review) {
        // Validate product exists
        if (!productRepository.existsById(review.getProduct().getId())) {
            return null;
        }

        // Validate customer exists
        if (!customerRepository.existsById(review.getCustomer().getId())) {
            return null;
        }

        // Validate customer has purchased the product
        if (!hasPurchasedProduct(review.getCustomer().getId(), review.getProduct().getId())) {
            return null;
        }

        // Validate rating
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            return null;
        }

        // Set review date if not provided
        if (review.getReviewDate() == null) {
            review.setReviewDate(Instant.now());
        }

        // Check if review already exists
        Optional<Review> existingReview = reviewRepository
            .findByProductIdAndCustomerId(
                review.getProduct().getId(),
                review.getCustomer().getId()
            );

        if (existingReview.isPresent()) {
            // Update existing review
            Review existing = existingReview.get();
            existing.setRating(review.getRating());
            existing.setComment(review.getComment());
            return reviewRepository.save(existing);
        } else {
            // Create new review
            return reviewRepository.save(review);
        }
    }

    private boolean hasPurchasedProduct(Integer customerId, Integer productId) {
        // Check if customer has any orders containing this product
        return orderDetailRepository.existsByOrderCustomerIdAndProductId(customerId, productId);
    }

    @Override
    public Optional<Review> getReviewById(Integer id) {
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> getReviewsByProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return List.of();
        }
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public List<Review> getReviewsByCustomer(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            return List.of();
        }
        return reviewRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<Review> getCustomerReviewForProduct(Integer customerId, Integer productId) {
        if (!customerRepository.existsById(customerId) || !productRepository.existsById(productId)) {
            return Optional.empty();
        }
        return reviewRepository.findByProductIdAndCustomerId(productId, customerId);
    }

    @Override
    public List<Review> getReviewsByRatingRange(Integer minRating, Integer maxRating) {
        if (minRating < 1 || maxRating > 5 || minRating > maxRating) {
            return List.of();
        }
        return reviewRepository.findByRatingBetween(minRating, maxRating);
    }

    @Override
    public Optional<Double> getAverageRatingForProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return Optional.empty();
        }
        return reviewRepository.calculateAverageRating(productId);
    }

    @Override
    public long getReviewCountForProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return 0L;
        }
        return reviewRepository.countByProductId(productId);
    }

    @Override
    public List<Review> getRecentReviews(int limit) {
        return reviewRepository.findTop5ByOrderByReviewDateDesc()
            .stream()
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> searchReviews(String searchTerm) {
        return reviewRepository.searchByComment(searchTerm);
    }

    @Override
    @Transactional
    public void deleteReview(Integer id) {
        reviewRepository.deleteById(id);
    }
}