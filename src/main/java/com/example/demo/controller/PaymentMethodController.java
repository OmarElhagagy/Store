package com.example.demo.controller;

import com.example.demo.dto.PaymentMethodDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        int length = cardNumber.length();
        return "*".repeat(length - 4) + cardNumber.substring(length - 4);
    }
} 