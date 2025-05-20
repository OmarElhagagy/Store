package com.example.demo.dto;

import com.example.demo.entities.CustomerOrder;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for CustomerOrder entities
 * Used for API responses with order details
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerOrderDTO {

    Integer id;
    
    @NotNull(message = "Employee seller ID is required")
    Integer employeeSellerId;
    
    @NotNull(message = "Customer ID is required")
    Integer customerId;
    
    @NotNull(message = "Payment method ID is required")
    Integer paymentMethodId;
    
    Integer addressId;
    
    Integer promotionId;
    
    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date must be in the past or present")
    LocalDate purchaseDate;
    
    @NotNull(message = "Total price is required")
    @PositiveOrZero(message = "Total price must be zero or positive")
    BigDecimal totalPrice;
    
    @NotBlank(message = "Status is required")
    String status;
    
    /**
     * Converts a CustomerOrder entity to CustomerOrderDTO
     *
     * @param order The CustomerOrder entity
     * @return CustomerOrderDTO
     */
    public static CustomerOrderDTO fromEntity(CustomerOrder order) {
        if (order == null) {
            return null;
        }
        
        return CustomerOrderDTO.builder()
                .id(order.getId())
                .employeeSellerId(order.getEmployeeSeller() != null ? order.getEmployeeSeller().getId() : null)
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .paymentMethodId(order.getPaymentMethod() != null ? order.getPaymentMethod().getId() : null)
                .addressId(order.getAddress() != null ? order.getAddress().getId() : null)
                .promotionId(order.getPromotion() != null ? order.getPromotion().getId() : null)
                .purchaseDate(order.getPurchaseDate())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .build();
    }
    
    /**
     * Updates an existing CustomerOrder entity with DTO data
     * Note: This doesn't set the associations which should be handled separately
     *
     * @param entity The CustomerOrder entity to update
     * @return Updated CustomerOrder entity
     */
    public CustomerOrder updateEntity(CustomerOrder entity) {
        entity.setPurchaseDate(this.purchaseDate);
        entity.setTotalPrice(this.totalPrice);
        entity.setStatus(this.status);
        return entity;
    }
}