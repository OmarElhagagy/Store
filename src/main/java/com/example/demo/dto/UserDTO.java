package com.example.demo.dto;

import com.example.demo.entities.User;
import com.fasterxml.jackson.annotation.JsonInclude;
<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.With;
=======
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044

import java.time.Instant;

/**
 * Data Transfer Object for User entities
 * Used for API responses with user details
 * Excludes sensitive data like password hash
 */
@Value
@Builder
<<<<<<< HEAD
@With
=======
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    Integer id;
    
    @NotNull(message = "Customer ID is required")
    Integer customerId;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email;
    
<<<<<<< HEAD
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password;
    
    @NotBlank(message = "First name is required")
    String firstName;
    
    @NotBlank(message = "Last name is required")
    String lastName;
    
=======
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
    @NotBlank(message = "Role is required")
    String role;
    
    Integer failedLogins;
    
    Instant lockedUntil;
    
    Instant createdAt;
    
    /**
     * Converts a User entity to UserDTO
     * Excludes sensitive password information
     *
     * @param user The User entity
     * @return UserDTO
     */
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .customerId(user.getCustomer() != null ? user.getCustomer().getId() : null)
                .email(user.getEmail())
<<<<<<< HEAD
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
=======
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
                .role(user.getRole())
                .failedLogins(user.getFailedLogins())
                .lockedUntil(user.getLockedUntil())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    /**
     * Updates an existing User entity with DTO data
     * Note: This doesn't set the password hash or customer association which should be handled separately
     *
     * @param entity The User entity to update
     * @return Updated User entity
     */
    public User updateEntity(User entity) {
        entity.setEmail(this.email);
<<<<<<< HEAD
        if (this.firstName != null) {
            entity.setFirstName(this.firstName);
        }
        if (this.lastName != null) {
            entity.setLastName(this.lastName);
        }
        entity.setRole(this.role);
        if (this.failedLogins != null) {
            entity.setFailedLogins(this.failedLogins);
        }
=======
        entity.setRole(this.role);
        entity.setFailedLogins(this.failedLogins);
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
        entity.setLockedUntil(this.lockedUntil);
        if (this.createdAt != null) {
            entity.setCreatedAt(this.createdAt);
        }
        return entity;
    }
}