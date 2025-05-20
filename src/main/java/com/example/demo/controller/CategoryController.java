package com.example.demo.controller;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entities.Category;
<<<<<<< HEAD
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
=======
import com.example.demo.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Category Controller", description = "API to manage product categories")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

=======
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

>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

<<<<<<< HEAD
    @GetMapping
    @Operation(summary = "Get all categories", description = "Returns a list of all product categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        logger.info("Fetching all categories");
=======
    /**
     * Get all categories
     */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        log.info("REST request to get all Categories");
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
        List<Category> categories = categoryService.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(CategoryDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

<<<<<<< HEAD
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Returns a single category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryDTO> getCategoryById(
            @Parameter(description = "Category ID", required = true) @PathVariable Integer id) {
        logger.info("Fetching category with ID: {}", id);
        
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        return ResponseEntity.ok(CategoryDTO.fromEntity(category));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create new category", description = "Creates a new product category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Category already exists")
    })
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        logger.info("Creating new category: {}", categoryDTO.getCategoryName());
        
        // Check if category already exists
        if (categoryService.existsByCategoryName(categoryDTO.getCategoryName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Category newCategory = new Category();
        newCategory.setCategoryName(categoryDTO.getCategoryName());
        newCategory.setDescription(categoryDTO.getDescription());
        
        Category savedCategory = categoryService.save(newCategory);
        
        return new ResponseEntity<>(CategoryDTO.fromEntity(savedCategory), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update category", description = "Updates an existing product category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "Category ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        logger.info("Updating category with ID: {}", id);
        
        Category existingCategory = categoryService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        // Check if category name already exists (excluding the current category)
        if (!existingCategory.getCategoryName().equals(categoryDTO.getCategoryName()) && 
                categoryService.existsByCategoryName(categoryDTO.getCategoryName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        existingCategory.setCategoryName(categoryDTO.getCategoryName());
        existingCategory.setDescription(categoryDTO.getDescription());
        
        Category updatedCategory = categoryService.save(existingCategory);
        return ResponseEntity.ok(CategoryDTO.fromEntity(updatedCategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete category", description = "Deletes a product category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category is in use by products")
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID", required = true) @PathVariable Integer id) {
        logger.info("Deleting category with ID: {}", id);
        
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        // Check if category has associated products using findByProductsIsNotEmpty method
        // which will give us a list of categories that have products
        List<Category> categoriesWithProducts = categoryService.findByProductsIsNotEmpty();
        boolean hasProducts = categoriesWithProducts.stream()
                .anyMatch(c -> c.getId().equals(id));
                
        if (hasProducts) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search categories", description = "Search categories by name")
    public ResponseEntity<List<CategoryDTO>> searchCategories(@RequestParam String name) {
        logger.info("Searching categories with name containing: {}", name);
        
        List<Category> categories = categoryService.findByCategoryNameContainingIgnoreCase(name);
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(CategoryDTO::fromEntity)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(categoryDTOs);
    }
=======
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
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
} 