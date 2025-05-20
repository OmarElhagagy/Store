package com.example.demo.controller;

import com.example.demo.dto.ShippingDTO;
import com.example.demo.entities.Shipping;
import com.example.demo.service.ShippingService;
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
 * REST Controller for managing shipping methods and shipment tracking
 */
@RestController
@RequestMapping("/api/shipping")
@Slf4j
public class ShippingController {

    private final ShippingService shippingService;

    @Autowired
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    /**
     * Get all shipping records
     * Restricted to admin and staff users
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ShippingDTO>> getAllShippingRecords() {
        log.info("REST request to get all Shipping records");
        List<Shipping> shippings = shippingService.findAll();
        List<ShippingDTO> shippingDTOs = shippings.stream()
                .map(ShippingDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(shippingDTOs);
    }

    /**
     * Get shipping records for a specific order
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @securityService.isOrderResourceOwner(#orderId)")
    public ResponseEntity<List<ShippingDTO>> getShippingByOrder(@PathVariable Integer orderId) {
        log.info("REST request to get Shipping records for order ID {}", orderId);
        List<Shipping> shippings = shippingService.findByOrderId(orderId);
        List<ShippingDTO> shippingDTOs = shippings.stream()
                .map(ShippingDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(shippingDTOs);
    }

    /**
     * Get shipping records for a specific customer
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @securityService.isCustomerResourceOwner(#customerId)")
    public ResponseEntity<List<ShippingDTO>> getShippingByCustomer(@PathVariable Integer customerId) {
        log.info("REST request to get Shipping records for customer ID {}", customerId);
        List<Shipping> shippings = shippingService.findByCustomerId(customerId);
        List<ShippingDTO> shippingDTOs = shippings.stream()
                .map(ShippingDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(shippingDTOs);
    }

    /**
     * Get shipping records by status
     * Restricted to admin and staff users
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<ShippingDTO>> getShippingByStatus(@PathVariable String status) {
        log.info("REST request to get Shipping records with status {}", status);
        List<Shipping> shippings = shippingService.findByStatus(status);
        List<ShippingDTO> shippingDTOs = shippings.stream()
                .map(ShippingDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(shippingDTOs);
    }

    /**
     * Get a specific shipping record by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF') or @securityService.isShippingResourceOwner(#id)")
    public ResponseEntity<ShippingDTO> getShipping(@PathVariable Integer id) {
        log.info("REST request to get Shipping record with ID {}", id);
        Optional<Shipping> shipping = shippingService.findById(id);
        return shipping
                .map(value -> ResponseEntity.ok(ShippingDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Track a shipment by tracking number (public endpoint)
     */
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<ShippingDTO> trackShipment(@PathVariable String trackingNumber) {
        log.info("REST request to track Shipment with tracking number {}", trackingNumber);
        Optional<Shipping> shipping = shippingService.findByTrackingNumber(trackingNumber);
        return shipping
                .map(value -> ResponseEntity.ok(ShippingDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new shipping record
     * Restricted to admin and staff users
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> createShipping(@Valid @RequestBody ShippingDTO shippingDTO, BindingResult bindingResult) {
        log.info("REST request to create Shipping record for order ID {}", shippingDTO.getOrderId());
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            Shipping shipping = shippingService.createShippingFromDTO(shippingDTO);
            return new ResponseEntity<>(ShippingDTO.fromEntity(shipping), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating shipping record", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating shipping record: " + e.getMessage(), e);
        }
    }

    /**
     * Update shipping status
     * Restricted to admin and staff users
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ShippingDTO> updateShippingStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        log.info("REST request to update status for Shipping ID {} to {}", id, status);
        try {
            Shipping shipping = shippingService.updateStatus(id, status, notes);
            return ResponseEntity.ok(ShippingDTO.fromEntity(shipping));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error updating shipping status", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating shipping status: " + e.getMessage(), e);
        }
    }

    /**
     * Update tracking information
     * Restricted to admin and staff users
     */
    @PutMapping("/{id}/tracking")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ShippingDTO> updateTrackingInfo(
            @PathVariable Integer id,
            @RequestParam String carrier,
            @RequestParam String trackingNumber,
            @RequestParam(required = false) String trackingUrl) {
        log.info("REST request to update tracking info for Shipping ID {}", id);
        try {
            Shipping shipping = shippingService.updateTrackingInfo(id, carrier, trackingNumber, trackingUrl);
            return ResponseEntity.ok(ShippingDTO.fromEntity(shipping));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error updating tracking information", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating tracking information: " + e.getMessage(), e);
        }
    }

    /**
     * Add shipping event/milestone
     * Restricted to admin and staff users
     */
    @PostMapping("/{id}/events")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ShippingDTO> addShippingEvent(
            @PathVariable Integer id,
            @RequestParam String eventType,
            @RequestParam String location,
            @RequestParam(required = false) String notes) {
        log.info("REST request to add shipping event {} for Shipping ID {}", eventType, id);
        try {
            Shipping shipping = shippingService.addShippingEvent(id, eventType, location, notes);
            return ResponseEntity.ok(ShippingDTO.fromEntity(shipping));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error adding shipping event", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding shipping event: " + e.getMessage(), e);
        }
    }

    /**
     * Mark a shipment as delivered
     * Restricted to admin and staff users
     */
    @PutMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ShippingDTO> markAsDelivered(
            @PathVariable Integer id,
            @RequestParam(required = false) String notes) {
        log.info("REST request to mark Shipping ID {} as delivered", id);
        try {
            Shipping shipping = shippingService.markAsDelivered(id, notes);
            return ResponseEntity.ok(ShippingDTO.fromEntity(shipping));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error marking shipment as delivered", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error marking shipment as delivered: " + e.getMessage(), e);
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