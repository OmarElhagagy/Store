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
    
    Instant shippedDate;
    
    Instant deliveredDate;
    
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
                .shippedDate(shipping.getShippedDate())
                .deliveredDate(shipping.getDeliveredDate())
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
        entity.setShippedDate(this.shippedDate);
        entity.setDeliveredDate(this.deliveredDate);
        return entity;
    }
}