package com.example.demo.dto;

import com.example.demo.entities.Employee;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/**
 * Data Transfer Object for Employee entities
 * Used for API responses with employee details
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDTO {

    Integer id;
    
    @NotNull(message = "Store ID is required")
    Integer storeId;
    
    Integer storageNo;
    
    @NotBlank(message = "First name is required")
    String fName;
    
    String mName;
    
    @NotBlank(message = "Last name is required")
    String lName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    String email;
    
    @NotBlank(message = "Gender is required")
    String gender;
    
    @NotBlank(message = "Role is required")
    String role;
    
    Integer supervisorId;
    
    /**
     * Converts an Employee entity to EmployeeDTO
     *
     * @param employee The Employee entity
     * @return EmployeeDTO
     */
    public static EmployeeDTO fromEntity(Employee employee) {
        if (employee == null) {
            return null;
        }
        
        return EmployeeDTO.builder()
                .id(employee.getId())
                .storeId(employee.getStore() != null ? employee.getStore().getId() : null)
                .storageNo(employee.getStorageNo() != null ? employee.getStorageNo().getId() : null)
                .fName(employee.getFName())
                .mName(employee.getMName())
                .lName(employee.getLName())
                .email(employee.getEmail())
                .gender(employee.getGender())
                .role(employee.getRole())
                .supervisorId(employee.getSupervisor() != null ? employee.getSupervisor().getId() : null)
                .build();
    }
    
    /**
     * Updates an existing Employee entity with DTO data
     * Note: This doesn't set the associations which should be handled separately
     *
     * @param entity The Employee entity to update
     * @return Updated Employee entity
     */
    public Employee updateEntity(Employee entity) {
        entity.setFName(this.fName);
        entity.setMName(this.mName);
        entity.setLName(this.lName);
        entity.setEmail(this.email);
        entity.setGender(this.gender);
        entity.setRole(this.role);
        return entity;
    }
}