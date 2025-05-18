package com.example.demo.dto;

import com.example.demo.entities.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Data Transfer Object for User entities
 * Used for API responses with user details
 * Excludes sensitive data like password hash
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    Integer id;
    
    @NotNull(message = "Customer ID is required")
    Integer customerId;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email;
    
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
        entity.setRole(this.role);
        entity.setFailedLogins(this.failedLogins);
        entity.setLockedUntil(this.lockedUntil);
        if (this.createdAt != null) {
            entity.setCreatedAt(this.createdAt);
        }
        return entity;
    }
}