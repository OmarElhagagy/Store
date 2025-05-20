package com.example.demo.dto;

import com.example.demo.entities.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

/**
 * Data Transfer Object for Address entities
 * Provides a simplified view of Address data for API responses
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {

    Integer id;
    
    Integer customerId;
    
    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street cannot exceed 100 characters")
    String street;
    
    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    String city;
    
    @Size(max = 50, message = "State cannot exceed 50 characters")
    String state;
    
    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    String postalCode;
    
    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    String country;
    
    @NotNull(message = "Default address flag is required")
    Boolean isDefault;

    /**
     * Converts an Address entity to AddressDTO
     *
     * @param address The Address entity
     * @return AddressDTO
     */
    public static AddressDTO fromEntity(Address address) {
        if (address == null) {
            return null;
        }
        
        return AddressDTO.builder()
                .id(address.getId())
                .customerId(address.getCustomer() != null ? address.getCustomer().getId() : null)
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.getIsDefault())
                .build();
    }
    
    /**
     * Applies DTO data to an existing Address entity
     * Note: This doesn't set the Customer association which should be handled separately
     *
     * @param entity The Address entity to update
     * @return Updated Address entity
     */
    public Address updateEntity(Address entity) {
        entity.setStreet(this.street);
        entity.setCity(this.city);
        entity.setState(this.state);
        entity.setPostalCode(this.postalCode);
        entity.setCountry(this.country);
        entity.setIsDefault(this.isDefault);
        return entity;
    }
}