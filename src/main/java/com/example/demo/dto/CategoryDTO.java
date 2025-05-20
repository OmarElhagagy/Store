package com.example.demo.dto;

import com.example.demo.entities.Category;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

/**
 * Data Transfer Object for Category entities
 * Used for API requests and responses related to product categories
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDTO {

    Integer id;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 50, message = "Category name cannot exceed 50 characters")
    String categoryName;
    
    /**
     * Converts a Category entity to CategoryDTO
     *
     * @param category The Category entity
     * @return CategoryDTO
     */
    public static CategoryDTO fromEntity(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategoryDTO.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .build();
    }
    
    /**
     * Updates an existing Category entity with DTO data
     *
     * @param entity The Category entity to update
     * @return Updated Category entity
     */
    public Category updateEntity(Category entity) {
        entity.setCategoryName(this.categoryName);
        return entity;
    }
    
    /**
     * Creates a new Category entity from this DTO
     * 
     * @return New Category entity
     */
    public Category toEntity() {
        Category category = new Category();
        category.setId(this.id);
        category.setCategoryName(this.categoryName);
        return category;
    }
}