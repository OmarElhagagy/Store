package com.example.demo.controller;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entities.Category;
import com.example.demo.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for managing product categories
 */
@RestController
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Get all categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        log.info("REST request to get all Categories");
        List<Category> categories = categoryService.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(CategoryDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Integer id) {
        log.info("REST request to get Category with ID {}", id);
        Optional<Category> category = categoryService.findById(id);
        return category
                .map(value -> ResponseEntity.ok(CategoryDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get subcategories by parent category ID
     */
    @GetMapping("/{id}/subcategories")
    public ResponseEntity<List<CategoryDTO>> getSubcategories(@PathVariable Integer id) {
        log.info("REST request to get subcategories for Category ID {}", id);
        List<Category> subcategories = categoryService.findSubcategoriesByParentId(id);
        List<CategoryDTO> categoryDTOs = subcategories.stream()
                .map(CategoryDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    /**
     * Get only top-level categories (categories without parents)
     */
    @GetMapping("/top-level")
    public ResponseEntity<List<CategoryDTO>> getTopLevelCategories() {
        log.info("REST request to get all top-level Categories");
        List<Category> topCategories = categoryService.findTopLevelCategories();
        List<CategoryDTO> categoryDTOs = topCategories.stream()
                .map(CategoryDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    /**
     * Create a new category
     * Restricted to admin users
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult bindingResult) {
        log.info("REST request to create a new Category");
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            Category category = new Category();
            categoryDTO.updateEntity(category);
            
            Category savedCategory = categoryService.createCategory(category);
            
            return new ResponseEntity<>(CategoryDTO.fromEntity(savedCategory), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating category", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating category: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing category
     * Restricted to admin users
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult bindingResult) {
        log.info("REST request to update Category with ID {}", id);
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            Category existingCategory = categoryService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID " + id));
            
            // Prevent circular parent-child relationships
            if (categoryDTO.getParentCategoryId() != null && categoryDTO.getParentCategoryId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category cannot be its own parent");
            }
            
            categoryDTO.updateEntity(existingCategory);
            
            Category updatedCategory = categoryService.updateCategory(existingCategory);
            
            return ResponseEntity.ok(CategoryDTO.fromEntity(updatedCategory));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating category", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating category: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a category
     * Restricted to admin users
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        log.info("REST request to delete Category with ID {}", id);
        try {
            // Check if category has subcategories
            List<Category> subcategories = categoryService.findSubcategoriesByParentId(id);
            if (!subcategories.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Cannot delete category with subcategories. Remove subcategories first."
                );
            }
            
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting category", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting category: " + e.getMessage(), e);
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