package com.example.demo.controller;

import com.example.demo.dto.PaymentMethodDTO;
import com.example.demo.entities.PaymentMethod;
import com.example.demo.service.PaymentMethodService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * REST Controller for managing payment methods
 */
@RestController
@RequestMapping("/api/payment-methods")
@Slf4j
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @Autowired
    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    /**
     * Get all payment methods for a customer
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#customerId)")
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethodsByCustomer(@PathVariable Integer customerId) {
        log.info("REST request to get Payment Methods for customer ID {}", customerId);
        List<PaymentMethod> paymentMethods = paymentMethodService.findByCustomerId(customerId);
        List<PaymentMethodDTO> paymentMethodDTOs = paymentMethods.stream()
                .map(PaymentMethodDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentMethodDTOs);
    }

    /**
     * Get default payment method for a customer
     */
    @GetMapping("/customer/{customerId}/default")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#customerId)")
    public ResponseEntity<PaymentMethodDTO> getDefaultPaymentMethod(@PathVariable Integer customerId) {
        log.info("REST request to get default Payment Method for customer ID {}", customerId);
        Optional<PaymentMethod> defaultMethod = paymentMethodService.findDefaultByCustomerId(customerId);
        return defaultMethod
                .map(method -> ResponseEntity.ok(PaymentMethodDTO.fromEntity(method)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get a specific payment method by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isPaymentMethodResourceOwner(#id)")
    public ResponseEntity<PaymentMethodDTO> getPaymentMethod(@PathVariable Integer id) {
        log.info("REST request to get Payment Method with ID {}", id);
        Optional<PaymentMethod> paymentMethod = paymentMethodService.findById(id);
        return paymentMethod
                .map(value -> ResponseEntity.ok(PaymentMethodDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new payment method
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#paymentMethodDTO.customerId)")
    public ResponseEntity<?> createPaymentMethod(@Valid @RequestBody PaymentMethodDTO paymentMethodDTO, 
                                               BindingResult bindingResult) {
        log.info("REST request to create Payment Method for customer ID {}", paymentMethodDTO.getCustomerId());
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            // Mask sensitive data in logs
            String maskedCardNumber = maskCardNumber(paymentMethodDTO.getCardNumber());
            log.info("Processing payment method with card number {}", maskedCardNumber);
            
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethodDTO.updateEntity(paymentMethod);
            
            PaymentMethod savedMethod = paymentMethodService.createPaymentMethod(paymentMethod);
            
            return new ResponseEntity<>(PaymentMethodDTO.fromEntity(savedMethod), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating payment method", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing payment method
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isPaymentMethodResourceOwner(#id)")
    public ResponseEntity<?> updatePaymentMethod(
            @PathVariable Integer id,
            @Valid @RequestBody PaymentMethodDTO paymentMethodDTO,
            BindingResult bindingResult) {
        log.info("REST request to update Payment Method with ID {}", id);
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            PaymentMethod existingMethod = paymentMethodService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment method not found with ID " + id));
            
            // Mask sensitive data in logs
            String maskedCardNumber = maskCardNumber(paymentMethodDTO.getCardNumber());
            log.info("Updating payment method with card number {}", maskedCardNumber);
            
            paymentMethodDTO.updateEntity(existingMethod);
            
            PaymentMethod updatedMethod = paymentMethodService.updatePaymentMethod(existingMethod);
            
            return ResponseEntity.ok(PaymentMethodDTO.fromEntity(updatedMethod));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error updating payment method", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a payment method
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isPaymentMethodResourceOwner(#id)")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Integer id) {
        log.info("REST request to delete Payment Method with ID {}", id);
        try {
            paymentMethodService.deletePaymentMethod(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting payment method", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Set a payment method as default for a customer
     */
    @PutMapping("/{id}/default")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isPaymentMethodResourceOwner(#id)")
    public ResponseEntity<PaymentMethodDTO> setDefaultPaymentMethod(@PathVariable Integer id) {
        log.info("REST request to set Payment Method ID {} as default", id);
        try {
            PaymentMethod defaultMethod = paymentMethodService.setDefaultPaymentMethod(id);
            return ResponseEntity.ok(PaymentMethodDTO.fromEntity(defaultMethod));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error setting default payment method", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error setting default payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Validate a payment method (for example, before processing a payment)
     */
    @PostMapping("/{id}/validate")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isPaymentMethodResourceOwner(#id)")
    public ResponseEntity<Map<String, Boolean>> validatePaymentMethod(@PathVariable Integer id) {
        log.info("REST request to validate Payment Method with ID {}", id);
        try {
            boolean isValid = paymentMethodService.validatePaymentMethod(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("valid", isValid);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error validating payment method", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error validating payment method: " + e.getMessage(), e);
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
    
    /**
     * Mask all but the last 4 digits of a card number for logging purposes
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        
        int length = cardNumber.length();
        String last4 = cardNumber.substring(length - 4);
        StringBuilder masked = new StringBuilder();
        
        for (int i = 0; i < length - 4; i++) {
            masked.append("*");
        }
        
        masked.append(last4);
        return masked.toString();
    }
} 