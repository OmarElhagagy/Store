package com.example.demo.dto;

import com.example.demo.entities.Image;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/**
 * Data Transfer Object for Image entities
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageDTO {
    
    Integer id;
    
    @NotNull(message = "Product ID is required")
    Integer productId;
    
    @NotBlank(message = "Image URL is required")
    String imageUrl;
    
    @NotNull(message = "Primary image flag is required")
    Boolean isPrimary;
    
    String altText;
    
    /**
     * Converts an Image entity to ImageDTO
     *
     * @param image The Image entity
     * @return ImageDTO
     */
    public static ImageDTO fromEntity(Image image) {
        if (image == null) {
            return null;
        }
        
        return ImageDTO.builder()
                .id(image.getId())
                .productId(image.getProduct() != null ? image.getProduct().getId() : null)
                .imageUrl(image.getImageUrl())
                .isPrimary(image.getIsPrimary())
                .altText(image.getAltText())
                .build();
    }
    
    /**
     * Updates an Image entity with data from this DTO
     *
     * @param image The Image entity to update
     */
    public void updateEntity(Image image) {
        // ID is not updated as it's the primary key
        // Product is not updated here as it should be handled separately
        image.setImageUrl(this.imageUrl);
        image.setIsPrimary(this.isPrimary);
        image.setAltText(this.altText);
    }
} 