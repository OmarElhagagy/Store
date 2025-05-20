package com.example.demo.dto;

import com.example.demo.entities.Notification;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Data Transfer Object for Notification entities
 * Used for API responses with notification details
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO {

    Integer id;
    
    Integer customerId;
    
    Integer storeId;
    
    Integer productId;
    
    Integer employeeId;
    
    @NotBlank(message = "Message is required")
    String message;
    
    @NotNull(message = "Sent date is required")
    @PastOrPresent(message = "Sent date must be in the past or present")
    LocalDate sentDate;
    
    /**
     * Converts a Notification entity to NotificationDTO
     *
     * @param notification The Notification entity
     * @return NotificationDTO
     */
    public static NotificationDTO fromEntity(Notification notification) {
        if (notification == null) {
            return null;
        }
        
        return NotificationDTO.builder()
                .id(notification.getId())
                .customerId(notification.getCustomer() != null ? notification.getCustomer().getId() : null)
                .storeId(notification.getStore() != null ? notification.getStore().getId() : null)
                .productId(notification.getProduct() != null ? notification.getProduct().getId() : null)
                .employeeId(notification.getEmployee() != null ? notification.getEmployee().getId() : null)
                .message(notification.getMessage())
                .sentDate(notification.getSentDate())
                .build();
    }
    
    /**
     * Updates an existing Notification entity with DTO data
     * Note: This doesn't set the associations which should be handled separately
     *
     * @param entity The Notification entity to update
     * @return Updated Notification entity
     */
    public Notification updateEntity(Notification entity) {
        entity.setMessage(this.message);
        entity.setSentDate(this.sentDate);
        return entity;
    }
}