package com.example.demo.controller;

import com.example.demo.dto.PromotionDTO;
import com.example.demo.entities.Promotion;
import com.example.demo.service.PromotionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for managing promotions and discounts
 */
@RestController
@RequestMapping("/api/promotions")
@Slf4j
public class PromotionController {

    private final PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /**
     * Get all promotions with pagination
     */
    @GetMapping
    public ResponseEntity<Page<PromotionDTO>> getAllPromotions(Pageable pageable) {
        log.info("REST request to get all Promotions with pagination");
        Page<Promotion> promotions = promotionService.findAllWithPagination(pageable);
        Page<PromotionDTO> promotionDTOs = promotions.map(PromotionDTO::fromEntity);
        return ResponseEntity.ok(promotionDTOs);
    }

    /**
     * Get active promotions (current date is between start and end date)
     */
    @GetMapping("/active")
    public ResponseEntity<List<PromotionDTO>> getActivePromotions() {
        log.info("REST request to get all active Promotions");
        List<Promotion> promotions = promotionService.findActivePromotions();
        List<PromotionDTO> promotionDTOs = promotions.stream()
                .map(PromotionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(promotionDTOs);
    }

    /**
     * Get promotions by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<PromotionDTO>> getPromotionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get Promotions between {} and {}", startDate, endDate);
        List<Promotion> promotions = promotionService.findByDateRange(startDate, endDate);
        List<PromotionDTO> promotionDTOs = promotions.stream()
                .map(PromotionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(promotionDTOs);
    }

    /**
     * Get promotions by type (percent off, fixed amount, etc.)
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PromotionDTO>> getPromotionsByType(@PathVariable String type) {
        log.info("REST request to get Promotions of type {}", type);
        List<Promotion> promotions = promotionService.findByType(type);
        List<PromotionDTO> promotionDTOs = promotions.stream()
                .map(PromotionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(promotionDTOs);
    }

    /**
     * Get promotions for a specific product
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PromotionDTO>> getPromotionsForProduct(@PathVariable Integer productId) {
        log.info("REST request to get Promotions for product ID {}", productId);
        List<Promotion> promotions = promotionService.findByProductId(productId);
        List<PromotionDTO> promotionDTOs = promotions.stream()
                .map(PromotionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(promotionDTOs);
    }

    /**
     * Get promotions for a specific category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PromotionDTO>> getPromotionsForCategory(@PathVariable Integer categoryId) {
        log.info("REST request to get Promotions for category ID {}", categoryId);
        List<Promotion> promotions = promotionService.findByCategoryId(categoryId);
        List<PromotionDTO> promotionDTOs = promotions.stream()
                .map(PromotionDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(promotionDTOs);
    }

    /**
     * Get a specific promotion by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PromotionDTO> getPromotion(@PathVariable Integer id) {
        log.info("REST request to get Promotion with ID {}", id);
        Optional<Promotion> promotion = promotionService.findById(id);
        return promotion
                .map(value -> ResponseEntity.ok(PromotionDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new promotion
     * Restricted to admin users
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO, BindingResult bindingResult) {
        log.info("REST request to create a new Promotion");
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            validatePromotionDates(promotionDTO);
            validateDiscountValues(promotionDTO);
            
            Promotion promotion = new Promotion();
            promotionDTO.updateEntity(promotion);
            
            Promotion savedPromotion = promotionService.createPromotion(promotion);
            
            return new ResponseEntity<>(PromotionDTO.fromEntity(savedPromotion), HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating promotion", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating promotion: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing promotion
     * Restricted to admin users
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePromotion(
            @PathVariable Integer id,
            @Valid @RequestBody PromotionDTO promotionDTO,
            BindingResult bindingResult) {
        log.info("REST request to update Promotion with ID {}", id);
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            validatePromotionDates(promotionDTO);
            validateDiscountValues(promotionDTO);
            
            Promotion existingPromotion = promotionService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found with ID " + id));
            
            promotionDTO.updateEntity(existingPromotion);
            
            Promotion updatedPromotion = promotionService.updatePromotion(existingPromotion);
            
            return ResponseEntity.ok(PromotionDTO.fromEntity(updatedPromotion));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating promotion", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating promotion: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a promotion
     * Restricted to admin users
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePromotion(@PathVariable Integer id) {
        log.info("REST request to delete Promotion with ID {}", id);
        try {
            promotionService.deletePromotion(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting promotion", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting promotion: " + e.getMessage(), e);
        }
    }

    /**
     * Activate a promotion
     * Restricted to admin users
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionDTO> activatePromotion(@PathVariable Integer id) {
        log.info("REST request to activate Promotion with ID {}", id);
        try {
            Promotion promotion = promotionService.activatePromotion(id);
            return ResponseEntity.ok(PromotionDTO.fromEntity(promotion));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error activating promotion", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error activating promotion: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivate a promotion
     * Restricted to admin users
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionDTO> deactivatePromotion(@PathVariable Integer id) {
        log.info("REST request to deactivate Promotion with ID {}", id);
        try {
            Promotion promotion = promotionService.deactivatePromotion(id);
            return ResponseEntity.ok(PromotionDTO.fromEntity(promotion));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error deactivating promotion", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deactivating promotion: " + e.getMessage(), e);
        }
    }

    /**
     * Apply promotion to cart (validate promo code)
     */
    @PostMapping("/validate-code")
    public ResponseEntity<Map<String, Object>> validatePromoCode(@RequestParam String promoCode) {
        log.info("REST request to validate promotion code: {}", promoCode);
        try {
            Optional<Promotion> promotion = promotionService.findByPromoCode(promoCode);
            
            Map<String, Object> response = new HashMap<>();
            if (promotion.isPresent() && promotionService.isPromotionActive(promotion.get())) {
                Promotion validPromotion = promotion.get();
                response.put("valid", true);
                response.put("promotion", PromotionDTO.fromEntity(validPromotion));
            } else {
                response.put("valid", false);
                response.put("message", "Invalid or expired promotion code");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error validating promotion code", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error validating promotion code: " + e.getMessage(), e);
        }
    }

    /**
     * Validate that start date is before end date
     */
    private void validatePromotionDates(PromotionDTO promotionDTO) {
        if (promotionDTO.getStartDate().isAfter(promotionDTO.getEndDate())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "Start date must be before end date"
            );
        }
    }

    /**
     * Validate discount values based on promotion type
     */
    private void validateDiscountValues(PromotionDTO promotionDTO) {
        if ("PERCENT".equalsIgnoreCase(promotionDTO.getType())) {
            if (promotionDTO.getDiscountPercent() == null || 
                promotionDTO.getDiscountPercent().compareTo(BigDecimal.ZERO) <= 0 || 
                promotionDTO.getDiscountPercent().compareTo(new BigDecimal(100)) > 0) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Discount percent must be between 1 and 100"
                );
            }
        } else if ("FIXED".equalsIgnoreCase(promotionDTO.getType())) {
            if (promotionDTO.getDiscountAmount() == null || 
                promotionDTO.getDiscountAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Discount amount must be greater than zero"
                );
            }
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