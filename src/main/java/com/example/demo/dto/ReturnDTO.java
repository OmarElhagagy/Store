package com.example.demo.dto;

import com.example.demo.entities.Return;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Return entities
 * Used for API requests and responses related to product returns
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReturnDTO {

    Integer id;
    
    @NotNull(message = "Order ID is required")
    Integer orderId;
    
    @NotNull(message = "Product ID is required")
    Integer productId;
    
    String productName;
    
    @NotNull(message = "Refunded amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Refunded amount must be greater than or equal to 0")
    BigDecimal refundedAmount;
    
    @NotNull(message = "Quantity returned is required")
    @Min(value = 1, message = "Quantity returned must be at least 1")
    Integer quantityReturned;
    
    @NotBlank(message = "Reason is required")
    @Size(max = 255, message = "Reason cannot exceed 255 characters")
    String reason;
    
    @NotNull(message = "Return date is required")
    @PastOrPresent(message = "Return date must be in the past or present")
    LocalDate returnDate;
    
    /**
     * Converts a Return entity to ReturnDTO
     *
     * @param returnEntity The Return entity
     * @return ReturnDTO
     */
    public static ReturnDTO fromEntity(Return returnEntity) {
        if (returnEntity == null) {
            return null;
        }
        
        return ReturnDTO.builder()
                .id(returnEntity.getId())
                .orderId(returnEntity.getOrder() != null ? returnEntity.getOrder().getId() : null)
                .productId(returnEntity.getProduct() != null ? returnEntity.getProduct().getId() : null)
                .productName(returnEntity.getProduct() != null ? returnEntity.getProduct().getProductName() : null)
                .refundedAmount(returnEntity.getRefundedAmount())
                .quantityReturned(returnEntity.getQuantityReturned())
                .reason(returnEntity.getReason())
                .returnDate(returnEntity.getReturnDate())
                .build();
    }
    
    /**
     * Updates an existing Return entity with DTO data
     * Note: This doesn't set the associations which should be handled separately
     *
     * @param entity The Return entity to update
     * @return Updated Return entity
     */
    public Return updateEntity(Return entity) {
        entity.setRefundedAmount(this.refundedAmount);
        entity.setQuantityReturned(this.quantityReturned);
        entity.setReason(this.reason);
        entity.setReturnDate(this.returnDate);
        return entity;
    }
}