package com.example.demo.dto;

import com.example.demo.entities.Promotion;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Promotion entities
 * Used for API requests and responses related to promotions
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionDTO {

    Integer id;
    
    @Size(max = 20, message = "Code cannot exceed 20 characters")
    String code;
    
    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    String description;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Discount percentage must be greater than or equal to 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Discount percentage cannot exceed 100")
    BigDecimal discountPercentage;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount must be greater than or equal to 0")
    BigDecimal discountAmount;
    
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    LocalDate startDate;
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    LocalDate endDate;
    
    @NotNull(message = "Active status is required")
    Boolean isActive;
    
    Integer maxUses;
    
    String type;
    
    /**
     * Get the type of promotion (percentage or fixed amount)
     * @return The promotion type
     */
    public String getType() {
        return discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0 ? 
            "PERCENTAGE" : "FIXED_AMOUNT";
    }
    
    /**
     * Get the discount percentage (alias for discountPercentage)
     * @return The discount percentage
     */
    public BigDecimal getDiscountPercent() {
        return discountPercentage;
    }
    
    /**
     * Converts a Promotion entity to PromotionDTO
     *
     * @param promotion The Promotion entity
     * @return PromotionDTO
     */
    public static PromotionDTO fromEntity(Promotion promotion) {
        if (promotion == null) {
            return null;
        }
        
        return PromotionDTO.builder()
                .id(promotion.getId())
                .code(promotion.getCode())
                .description(promotion.getDescription())
                .discountPercentage(promotion.getDiscountPercentage())
                .discountAmount(promotion.getDiscountAmount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .isActive(promotion.getIsActive())
                .maxUses(promotion.getMaxUses())
                .type(promotion.getDiscountPercentage() != null && promotion.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0 ? 
                    "PERCENTAGE" : "FIXED_AMOUNT")
                .build();
    }
    
    /**
     * Updates an existing Promotion entity with DTO data
     *
     * @param entity The Promotion entity to update
     * @return Updated Promotion entity
     */
    public Promotion updateEntity(Promotion entity) {
        entity.setCode(this.code);
        entity.setDescription(this.description);
        entity.setDiscountPercentage(this.discountPercentage);
        entity.setDiscountAmount(this.discountAmount);
        entity.setStartDate(this.startDate);
        entity.setEndDate(this.endDate);
        entity.setIsActive(this.isActive);
        entity.setMaxUses(this.maxUses);
        return entity;
    }
}