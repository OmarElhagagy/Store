package com.example.demo.dto;

import com.example.demo.entities.Store;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/**
 * Data Transfer Object for Store entities
 * Used for API requests and responses related to store information
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreDTO {

    Integer id;
    
    @NotBlank(message = "Address is required")
    String address;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email;
    
    @NotNull(message = "Minimum stock is required")
    @Min(value = 0, message = "Minimum stock must be at least 0")
    Integer minStock;
    
    /**
     * Converts a Store entity to StoreDTO
     *
     * @param store The Store entity
     * @return StoreDTO
     */
    public static StoreDTO fromEntity(Store store) {
        if (store == null) {
            return null;
        }
        
        return StoreDTO.builder()
                .id(store.getId())
                .address(store.getAddress())
                .email(store.getEmail())
                .minStock(store.getMinStock())
                .build();
    }
    
    /**
     * Updates an existing Store entity with DTO data
     *
     * @param entity The Store entity to update
     * @return Updated Store entity
     */
    public Store updateEntity(Store entity) {
        entity.setAddress(this.address);
        entity.setEmail(this.email);
        entity.setMinStock(this.minStock);
        return entity;
    }
}