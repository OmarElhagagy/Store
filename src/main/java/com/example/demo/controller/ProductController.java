package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entities.Product;
import com.example.demo.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST Controller for managing products
 */
@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Get all products with pagination
     */
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(Pageable pageable) {
        log.info("REST request to get all Products with pagination");
        Page<Product> products = productService.findAllWithPagination(pageable);
        Page<ProductDTO> productDTOs = products.map(ProductDTO::fromEntity);
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * Get products by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Integer categoryId) {
        log.info("REST request to get Products by category ID {}", categoryId);
        List<Product> products = productService.findByCategoryId(categoryId);
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * Search products by name or description
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String query) {
        log.info("REST request to search Products with query: {}", query);
        List<Product> products = productService.searchProducts(query);
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    /**
     * Get a specific product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Integer id) {
        log.info("REST request to get Product with ID {}", id);
        Optional<Product> product = productService.findById(id);
        return product
                .map(value -> ResponseEntity.ok(ProductDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new product
     * Restricted to admin and staff users
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO, BindingResult bindingResult) {
        log.info("REST request to create a new Product");
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            Product product = new Product();
            productDTO.updateEntity(product);
            
            Product savedProduct = productService.createProduct(product);
            
            return new ResponseEntity<>(ProductDTO.fromEntity(savedProduct), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating product", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating product: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing product
     * Restricted to admin and staff users
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, 
                                          @Valid @RequestBody ProductDTO productDTO,
                                          BindingResult bindingResult) {
        log.info("REST request to update Product with ID {}", id);
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            Product existingProduct = productService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID " + id));
            
            productDTO.updateEntity(existingProduct);
            
            Product updatedProduct = productService.updateProduct(existingProduct);
            
            return ResponseEntity.ok(ProductDTO.fromEntity(updatedProduct));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error updating product", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating product: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a product
     * Restricted to admin users
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        log.info("REST request to delete Product with ID {}", id);
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting product", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting product: " + e.getMessage(), e);
        }
    }

    /**
     * Update product inventory
     * Restricted to admin and staff users
     */
    @PutMapping("/{id}/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ProductDTO> updateInventory(@PathVariable Integer id, @RequestParam Integer quantity) {
        log.info("REST request to update inventory for Product ID {} with quantity {}", id, quantity);
        try {
            Product product = productService.updateInventory(id, quantity);
            return ResponseEntity.ok(ProductDTO.fromEntity(product));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error updating product inventory", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating inventory: " + e.getMessage(), e);
        }
    }

    /**
     * Update product price
     * Restricted to admin users
     */
    @PutMapping("/{id}/price")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updatePrice(@PathVariable Integer id, @RequestParam Double price) {
        log.info("REST request to update price for Product ID {} to {}", id, price);
        try {
            if (price <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be greater than zero");
            }
            
            Product product = productService.updatePrice(id, price);
            return ResponseEntity.ok(ProductDTO.fromEntity(product));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating product price", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating price: " + e.getMessage(), e);
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