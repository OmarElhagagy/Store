package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

/**
 * Data Transfer Object for search suggestions
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchSuggestionDTO {
    
    // Type of suggestion (e.g., PRODUCT, CATEGORY, BRAND)
    String type;
    
    // The suggestion text
    String text;
    
    // ID associated with the suggestion (e.g., product ID, category ID)
    Integer id;
    
    // Optional display image URL (e.g., product image)
    String imageUrl;
    
    // Relevance score of the suggestion
    Double relevance;
} 