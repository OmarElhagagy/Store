package com.example.demo.dto;

import com.example.demo.entities.Supplier;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

/**
 * Data Transfer Object for Supplier entities
 * Used for API requests and responses related to supplier information
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplierDTO {

    Integer id;
    
    @NotBlank(message = "Supplier name is required")
    @Size(max = 100, message = "Supplier name cannot exceed 100 characters")
    String supplierName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email;
    
    /**
     * Converts a Supplier entity to SupplierDTO
     *
     * @param supplier The Supplier entity
     * @return SupplierDTO
     */
    public static SupplierDTO fromEntity(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        
        return SupplierDTO.builder()
                .id(supplier.getId())
                .supplierName(supplier.getSupplierName())
                .email(supplier.getEmail())
                .build();
    }
    
    /**
     * Updates an existing Supplier entity with DTO data
     *
     * @param entity The Supplier entity to update
     * @return Updated Supplier entity
     */
    public Supplier updateEntity(Supplier entity) {
        entity.setSupplierName(this.supplierName);
        entity.setEmail(this.email);
        return entity;
    }
}