package com.example.demo.dto;

import com.example.demo.entities.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Product entities
 * Used for API responses with full product details
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {

    Integer id;
    
    Integer supplierId;
    
    String supplierName;
    
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name cannot exceed 100 characters")
    String productName;
    
    @NotBlank(message = "Size is required")
    @Size(max = 10, message = "Size cannot exceed 10 characters")
    String size;
    
    @NotBlank(message = "Brand is required")
    @Size(max = 50, message = "Brand cannot exceed 50 characters")
    String brand;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    BigDecimal price;
    
    @NotBlank(message = "Color is required")
    @Size(max = 30, message = "Color cannot exceed 30 characters")
    String color;
    
    @NotNull(message = "Launch date is required")
    LocalDate launchDate;
    
    String description;
    
    @NotNull(message = "Active status is required")
    Boolean isActive;
    
    List<CategoryDTO> categories;
    
    /**
     * Converts a Product entity to ProductDTO
     *
     * @param product The Product entity
     * @return ProductDTO
     */
    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }
        
        List<CategoryDTO> categoryDTOs = product.getCategories() != null
                ? product.getCategories().stream()
                    .map(CategoryDTO::fromEntity)
                    .collect(Collectors.toList())
                : Collections.emptyList();
        
        return ProductDTO.builder()
                .id(product.getId())
                .supplierId(product.getSupplier() != null ? product.getSupplier().getId() : null)
                .supplierName(product.getSupplier() != null ? product.getSupplier().getSupplierName() : null)
                .productName(product.getProductName())
                .size(product.getSize())
                .brand(product.getBrand())
                .price(product.getPrice())
                .color(product.getColor())
                .launchDate(product.getLaunchDate())
                .description(product.getDescription())
                .isActive(product.getIsActive())
                .categories(categoryDTOs)
                .build();
    }
    
    /**
     * Updates an existing Product entity with DTO data
     * Note: This doesn't set the categories and supplier associations which should be handled separately
     *
     * @param entity The Product entity to update
     * @return Updated Product entity
     */
    public Product updateEntity(Product entity) {
        entity.setProductName(this.productName);
        entity.setSize(this.size);
        entity.setBrand(this.brand);
        entity.setPrice(this.price);
        entity.setColor(this.color);
        entity.setLaunchDate(this.launchDate);
        entity.setDescription(this.description);
        entity.setIsActive(this.isActive);
        return entity;
    }
}