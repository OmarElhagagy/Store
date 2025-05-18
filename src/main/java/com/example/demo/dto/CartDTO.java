package com.example.demo.dto;

import com.example.demo.entities.Cart;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Data Transfer Object for Cart entities
 * Used for API requests and responses related to shopping cart items
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDTO {

    Integer id;
    
    @NotNull(message = "Customer ID is required")
    Integer customerId;
    
    @NotNull(message = "Product ID is required")
    Integer productId;
    
    String productName;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity;
    
    LocalDate addedDate;
    
    /**
     * Converts a Cart entity to CartDTO
     *
     * @param cart The Cart entity
     * @return CartDTO
     */
    public static CartDTO fromEntity(Cart cart) {
        if (cart == null) {
            return null;
        }
        
        return CartDTO.builder()
                .id(cart.getId())
                .customerId(cart.getCustomer() != null ? cart.getCustomer().getId() : null)
                .productId(cart.getProduct() != null ? cart.getProduct().getId() : null)
                .productName(cart.getProduct() != null ? cart.getProduct().getProductName() : null)
                .quantity(cart.getQuantity())
                .addedDate(cart.getAddedDate())
                .build();
    }
    
    /**
     * Updates an existing Cart entity with DTO data
     * Note: This doesn't set the Customer and Product associations which should be handled separately
     *
     * @param entity The Cart entity to update
     * @return Updated Cart entity
     */
    public Cart updateEntity(Cart entity) {
        entity.setQuantity(this.quantity);
        if (this.addedDate != null) {
            entity.setAddedDate(this.addedDate);
        }
        return entity;
    }
}