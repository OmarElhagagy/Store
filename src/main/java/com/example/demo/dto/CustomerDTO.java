package com.example.demo.dto;

import com.example.demo.entities.Customer;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * Data Transfer Object for Customer entities
 * Used for API responses with customer details
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO {

    Integer id;
    
    @NotBlank(message = "First name is required")
    String fName;
    
    String mName;
    
    @NotBlank(message = "Last name is required")
    String lName;
    
    @NotBlank(message = "Gender is required")
    String gender;
    
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    LocalDate birthDate;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email;
    
    @NotNull(message = "Active status is required")
    Boolean active;
    
    /**
     * Converts a Customer entity to CustomerDTO
     *
     * @param customer The Customer entity
     * @return CustomerDTO
     */
    public static CustomerDTO fromEntity(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        return CustomerDTO.builder()
                .id(customer.getId())
                .fName(customer.getFName())
                .mName(customer.getMName())
                .lName(customer.getLName())
                .gender(customer.getGender())
                .birthDate(customer.getBirthDate())
                .email(customer.getEmail())
                .active(customer.isActive())
                .build();
    }
    
    /**
     * Updates an existing Customer entity with DTO data
     *
     * @param entity The Customer entity to update
     * @return Updated Customer entity
     */
    public Customer updateEntity(Customer entity) {
        entity.setFName(this.fName);
        entity.setMName(this.mName);
        entity.setLName(this.lName);
        entity.setGender(this.gender);
        entity.setBirthDate(this.birthDate);
        entity.setEmail(this.email);
        entity.setActive(this.active);
        return entity;
    }
}