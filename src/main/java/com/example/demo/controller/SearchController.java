package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.SearchRequestDTO;
import com.example.demo.dto.SearchSuggestionDTO;
import com.example.demo.dto.SearchAnalyticsDTO;
import com.example.demo.service.SearchService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for handling product search functionality
 */
@RestController
@RequestMapping("/api/search")
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Basic search endpoint for products
     */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable) {
        
        log.info("REST request to search products with query: {}", query);
        Page<ProductDTO> results = searchService.searchProducts(query, category, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Advanced search with complex filters
     */
    @PostMapping("/products/advanced")
    public ResponseEntity<?> advancedSearch(
            @Valid @RequestBody SearchRequestDTO searchRequest,
            BindingResult bindingResult,
            Pageable pageable) {
        
        log.info("REST request to perform advanced search");
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            Page<ProductDTO> results = searchService.advancedSearch(searchRequest, pageable);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error performing advanced search", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error performing search: " + e.getMessage(), e);
        }
    }

    /**
     * Get search suggestions based on partial input
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<SearchSuggestionDTO>> getSuggestions(@RequestParam String query) {
        log.info("REST request to get search suggestions for: {}", query);
        List<SearchSuggestionDTO> suggestions = searchService.getSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get search analytics (popular searches, conversion rates, etc.)
     * Restricted to admin and staff users
     */
    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<SearchAnalyticsDTO> getSearchAnalytics(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        
        log.info("REST request to get search analytics");
        SearchAnalyticsDTO analytics = searchService.getAnalytics(fromDate, toDate);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Reindex search data
     * Restricted to admin users
     */
    @PostMapping("/reindex")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> reindexSearch() {
        log.info("REST request to reindex search data");
        try {
            searchService.reindexAll();
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Search reindexing initiated");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error reindexing search data", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error reindexing search data: " + e.getMessage(), e);
        }
    }

    /**
     * Handle validation errors and return appropriate response
     */
    private ResponseEntity<Map<String, String>> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
} 