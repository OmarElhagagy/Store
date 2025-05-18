package com.example.demo.dto;

import com.example.demo.entities.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

/**
 * Data Transfer Object for PaymentMethod entities
 * Used for API requests and responses related to payment methods
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentMethodDTO {

    Integer id;
    
    @NotBlank(message = "Method name is required")
    @Size(max = 50, message = "Method name cannot exceed 50 characters")
    String methodName;
    
    /**
     * Converts a PaymentMethod entity to PaymentMethodDTO
     *
     * @param paymentMethod The PaymentMethod entity
     * @return PaymentMethodDTO
     */
    public static PaymentMethodDTO fromEntity(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }
        
        return PaymentMethodDTO.builder()
                .id(paymentMethod.getId())
                .methodName(paymentMethod.getMethodName())
                .build();
    }
    
    /**
     * Updates an existing PaymentMethod entity with DTO data
     *
     * @param entity The PaymentMethod entity to update
     * @return Updated PaymentMethod entity
     */
    public PaymentMethod updateEntity(PaymentMethod entity) {
        entity.setMethodName(this.methodName);
        return entity;
    }
    
    /**
     * Creates a new PaymentMethod entity from this DTO
     * 
     * @return New PaymentMethod entity
     */
    public PaymentMethod toEntity() {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(this.id);
        paymentMethod.setMethodName(this.methodName);
        return paymentMethod;
    }
}