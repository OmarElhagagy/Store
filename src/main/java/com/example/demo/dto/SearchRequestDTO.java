package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for search requests
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchRequestDTO {
    
    // General search query
    String query;
    
    // Filter fields
    Integer categoryId;
    List<Integer> categoryIds;
    String brand;
    String color;
    String size;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    Boolean inStock;
    Integer minRating;
    
    // Sorting fields
    String sortBy;
    String sortDirection;
    
    // Pagination fields
    Integer page;
    Integer pageSize;
} 