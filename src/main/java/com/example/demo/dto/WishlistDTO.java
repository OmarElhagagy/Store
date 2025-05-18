package com.example.demo.dto;

import com.example.demo.entities.Wishlist;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Data Transfer Object for Wishlist entities
 * Used for API requests and responses related to customer wishlists
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WishlistDTO {

    Integer id;
    
    @NotNull(message = "Customer ID is required")
    Integer customerId;
    
    @NotNull(message = "Product ID is required")
    Integer productId;
    
    String productName;
    
    LocalDate addedDate;
    
    /**
     * Converts a Wishlist entity to WishlistDTO
     *
     * @param wishlist The Wishlist entity
     * @return WishlistDTO
     */
    public static WishlistDTO fromEntity(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }
        
        return WishlistDTO.builder()
                .id(wishlist.getId())
                .customerId(wishlist.getCustomer() != null ? wishlist.getCustomer().getId() : null)
                .productId(wishlist.getProduct() != null ? wishlist.getProduct().getId() : null)
                .productName(wishlist.getProduct() != null ? wishlist.getProduct().getProductName() : null)
                .addedDate(wishlist.getAddedDate())
                .build();
    }
    
    /**
     * Updates an existing Wishlist entity with DTO data
     * Note: This doesn't set the associations which should be handled separately
     *
     * @param entity The Wishlist entity to update
     * @return Updated Wishlist entity
     */
    public Wishlist updateEntity(Wishlist entity) {
        if (this.addedDate != null) {
            entity.setAddedDate(this.addedDate);
        }
        return entity;
    }
}