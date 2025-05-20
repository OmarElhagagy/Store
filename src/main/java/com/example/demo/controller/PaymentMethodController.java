package com.example.demo.controller;

import com.example.demo.dto.PaymentMethodDTO;
<<<<<<< HEAD
import com.example.demo.entities.Customer;
import com.example.demo.entities.PaymentMethod;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.CustomerService;
import com.example.demo.service.PaymentMethodService;
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
=======
import com.example.demo.entities.PaymentMethod;
import com.example.demo.service.PaymentMethodService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payment-methods")
@Tag(name = "Payment Method Controller", description = "API to manage customer payment methods")
public class PaymentMethodController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentMethodController.class);

    private final PaymentMethodService paymentMethodService;
    private final CustomerService customerService;

    @Autowired
    public PaymentMethodController(PaymentMethodService paymentMethodService, CustomerService customerService) {
        this.paymentMethodService = paymentMethodService;
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all payment methods", description = "Returns a list of all payment methods")
    public ResponseEntity<List<PaymentMethodDTO>> getAllPaymentMethods() {
        logger.info("Fetching all payment methods");
        List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods();
        List<PaymentMethodDTO> paymentMethodDTOs = paymentMethods.stream()
                .map(PaymentMethodDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentMethodDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentMethodAuthorizationService.isPaymentMethodAuthorized(authentication, #id)")
    @Operation(summary = "Get payment method by ID", description = "Returns a single payment method by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment method found",
                    content = @Content(schema = @Schema(implementation = PaymentMethodDTO.class))),
            @ApiResponse(responseCode = "404", description = "Payment method not found")
    })
    public ResponseEntity<PaymentMethodDTO> getPaymentMethodById(
            @Parameter(description = "Payment method ID", required = true) @PathVariable Integer id) {
        logger.info("Fetching payment method with ID: {}", id);
        
        PaymentMethod paymentMethod = paymentMethodService.findPaymentMethodById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method", "id", id));
        
        return ResponseEntity.ok(PaymentMethodDTO.fromEntity(paymentMethod));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @customerAuthorizationService.isCustomerAuthorized(authentication, #customerId)")
    @Operation(summary = "Get payment methods by customer", description = "Returns payment methods for a specific customer")
    public ResponseEntity<List<PaymentMethodDTO>> getPaymentMethodsByCustomer(@PathVariable Integer customerId) {
        logger.info("Fetching payment methods for customer ID: {}", customerId);
        
        Customer customer = customerService.findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        
        List<PaymentMethod> paymentMethods = paymentMethodService.getPaymentMethodsByCustomer(customer);
        List<PaymentMethodDTO> paymentMethodDTOs = paymentMethods.stream()
                .map(PaymentMethodDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(paymentMethodDTOs);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @customerAuthorizationService.isCustomerAuthorized(authentication, #paymentMethodDTO.customerId)")
    @Operation(summary = "Create new payment method", description = "Creates a new payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment method created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<PaymentMethodDTO> createPaymentMethod(@Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        logger.info("Creating new payment method for customer ID: {}", paymentMethodDTO.getCustomerId());
        
        Customer customer = customerService.findCustomerById(paymentMethodDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", paymentMethodDTO.getCustomerId()));
        
        // Mask sensitive card information before logging
        String maskedCardNumber = maskCardNumber(paymentMethodDTO.getCardNumber());
        logger.info("Processing payment method with card number: {}", maskedCardNumber);
        
        PaymentMethod paymentMethod = paymentMethodService.createPaymentMethod(
                customer,
                paymentMethodDTO.getCardType(),
                paymentMethodDTO.getCardNumber(),
                paymentMethodDTO.getCardholderName(),
                paymentMethodDTO.getExpirationDate(),
                paymentMethodDTO.getIsDefault()
        );
        
        // Mask card number in response
        PaymentMethodDTO responseDTO = PaymentMethodDTO.fromEntity(paymentMethod);
        responseDTO.setCardNumber(maskedCardNumber);
        
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentMethodAuthorizationService.isPaymentMethodAuthorized(authentication, #id)")
    @Operation(summary = "Update payment method", description = "Updates an existing payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment method updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Payment method not found")
    })
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(
            @Parameter(description = "Payment method ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody PaymentMethodDTO paymentMethodDTO) {
        logger.info("Updating payment method with ID: {}", id);
        
        PaymentMethod existingPaymentMethod = paymentMethodService.findPaymentMethodById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method", "id", id));
        
        // Update payment method fields
        existingPaymentMethod.setCardType(paymentMethodDTO.getCardType());
        existingPaymentMethod.setCardholderName(paymentMethodDTO.getCardholderName());
        existingPaymentMethod.setExpirationDate(paymentMethodDTO.getExpirationDate());
        
        // Only update card number if provided and different
        if (paymentMethodDTO.getCardNumber() != null && !paymentMethodDTO.getCardNumber().isEmpty() && 
                !paymentMethodDTO.getCardNumber().equals(existingPaymentMethod.getCardNumber())) {
            existingPaymentMethod.setCardNumber(paymentMethodDTO.getCardNumber());
        }
        
        // Only update default status if provided
        if (paymentMethodDTO.getIsDefault() != null) {
            existingPaymentMethod.setIsDefault(paymentMethodDTO.getIsDefault());
            
            // If setting as default, unset other payment methods as default
            if (paymentMethodDTO.getIsDefault()) {
                paymentMethodService.setAsDefaultPaymentMethod(existingPaymentMethod);
            }
        }
        
        PaymentMethod updatedPaymentMethod = paymentMethodService.updatePaymentMethod(existingPaymentMethod);
        
        // Mask card number in response
        PaymentMethodDTO responseDTO = PaymentMethodDTO.fromEntity(updatedPaymentMethod);
        responseDTO.setCardNumber(maskCardNumber(updatedPaymentMethod.getCardNumber()));
        
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @paymentMethodAuthorizationService.isPaymentMethodAuthorized(authentication, #id)")
    @Operation(summary = "Delete payment method", description = "Deletes a payment method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment method deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payment method not found")
    })
    public ResponseEntity<Void> deletePaymentMethod(
            @Parameter(description = "Payment method ID", required = true) @PathVariable Integer id) {
        logger.info("Deleting payment method with ID: {}", id);
        
        PaymentMethod paymentMethod = paymentMethodService.findPaymentMethodById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method", "id", id));
        
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/default")
    @PreAuthorize("hasRole('ADMIN') or @paymentMethodAuthorizationService.isPaymentMethodAuthorized(authentication, #id)")
    @Operation(summary = "Set payment method as default", description = "Sets a payment method as the default for a customer")
    public ResponseEntity<PaymentMethodDTO> setPaymentMethodAsDefault(@PathVariable Integer id) {
        logger.info("Setting payment method ID: {} as default", id);
        
        PaymentMethod paymentMethod = paymentMethodService.findPaymentMethodById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method", "id", id));
        
        paymentMethod = paymentMethodService.setAsDefaultPaymentMethod(paymentMethod);
        
        // Mask card number in response
        PaymentMethodDTO responseDTO = PaymentMethodDTO.fromEntity(paymentMethod);
        responseDTO.setCardNumber(maskCardNumber(paymentMethod.getCardNumber()));
        
        return ResponseEntity.ok(responseDTO);
    }
    
    /**
     * Helper method to mask all but the last 4 digits of a card number
=======
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
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
<<<<<<< HEAD
        int length = cardNumber.length();
        return "*".repeat(length - 4) + cardNumber.substring(length - 4);
=======
        
        int length = cardNumber.length();
        String last4 = cardNumber.substring(length - 4);
        StringBuilder masked = new StringBuilder();
        
        for (int i = 0; i < length - 4; i++) {
            masked.append("*");
        }
        
        masked.append(last4);
        return masked.toString();
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
    }
} 