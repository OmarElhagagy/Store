package com.example.demo.dto;

import com.example.demo.entities.Shipping;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Data Transfer Object for Shipping entities
 * Used for API requests and responses related to order shipping information
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShippingDTO {

    Integer id;
    
    @NotNull(message = "Order ID is required")
    Integer orderId;
    
    @Size(max = 50, message = "Tracking number cannot exceed 50 characters")
    String trackingNumber;
    
    @Size(max = 50, message = "Shipping provider cannot exceed 50 characters")
    String shippingProvider;
    
<<<<<<< HEAD
    Instant shippedDate;
    
    Instant deliveredDate;
=======
    String shippedDate;
    
    String deliveredDate;
    
    // Added for estimated delivery
    String estimatedDeliveryDate;
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
    
    /**
     * Converts a Shipping entity to ShippingDTO
     *
     * @param shipping The Shipping entity
     * @return ShippingDTO
     */
    public static ShippingDTO fromEntity(Shipping shipping) {
        if (shipping == null) {
            return null;
        }
        
        return ShippingDTO.builder()
                .id(shipping.getId())
                .orderId(shipping.getOrder() != null ? shipping.getOrder().getId() : null)
                .trackingNumber(shipping.getTrackingNumber())
                .shippingProvider(shipping.getShippingProvider())
<<<<<<< HEAD
                .shippedDate(shipping.getShippedDate())
                .deliveredDate(shipping.getDeliveredDate())
=======
                .shippedDate(shipping.getShippedDate() != null ? shipping.getShippedDate().toString() : null)
                .deliveredDate(shipping.getDeliveredDate() != null ? shipping.getDeliveredDate().toString() : null)
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
                .build();
    }
    
    /**
     * Updates an existing Shipping entity with DTO data
     * Note: This doesn't set the order association which should be handled separately
     *
     * @param entity The Shipping entity to update
     * @return Updated Shipping entity
     */
    public Shipping updateEntity(Shipping entity) {
        entity.setTrackingNumber(this.trackingNumber);
        entity.setShippingProvider(this.shippingProvider);
<<<<<<< HEAD
        entity.setShippedDate(this.shippedDate);
        entity.setDeliveredDate(this.deliveredDate);
        return entity;
    }
=======
        
        // Convert string dates to Instant safely
        if (this.shippedDate != null && !this.shippedDate.isEmpty()) {
            entity.setShippedDate(Instant.parse(this.shippedDate));
        }
        
        if (this.deliveredDate != null && !this.deliveredDate.isEmpty()) {
            entity.setDeliveredDate(Instant.parse(this.deliveredDate));
        }
        
        return entity;
    }
    
    /**
     * Safely converts string to Instant
     * 
     * @param dateString ISO-8601 formatted date string
     * @return Instant object or null if string was null/empty
     */
    public static Instant parseInstant(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        return Instant.parse(dateString);
    }
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
}