package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
<<<<<<< HEAD
import com.example.demo.entities.Category;
import com.example.demo.entities.Product;
import com.example.demo.entities.Supplier;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import com.example.demo.service.SupplierService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
=======
import com.example.demo.entities.Product;
import com.example.demo.entities.Supplier;
import com.example.demo.service.ProductService;
import com.example.demo.service.SupplierService;
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
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

<<<<<<< HEAD
@RestController
@RequestMapping("/api/v1/products")
@Validated
@Tag(name = "Product Controller", description = "API to manage products in the store")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SupplierService supplierService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService, SupplierService supplierService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Returns a list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class)))
    })
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
=======
/**
 * REST Controller for managing products
 */
@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final SupplierService supplierService;

    @Autowired
    public ProductController(ProductService productService, SupplierService supplierService) {
        this.productService = productService;
        this.supplierService = supplierService;
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
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

<<<<<<< HEAD
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Returns a single product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Product ID", required = true) @PathVariable Integer id) {
        logger.info("Fetching product with ID: {}", id);
        return productService.findProductById(id)
                .map(ProductDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create new product", description = "Creates a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Product already exists")
    })
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        logger.info("Creating new product: {}", productDTO);
        
        // Check if product already exists
        if (productService.productExistsByNameAndBrand(productDTO.getProductName(), productDTO.getBrand())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                "Product already exists with name: " + productDTO.getProductName() + " and brand: " + productDTO.getBrand());
        }

        // Get supplier
        Optional<Supplier> supplier = supplierService.findSupplierById(productDTO.getSupplierId());
        if (productDTO.getSupplierId() != null && supplier.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier not found with ID: " + productDTO.getSupplierId());
        }

        // Create product
        Product newProduct = productService.createProduct(
=======
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
            // Extract supplier from DTO
            Supplier supplier = findSupplier(productDTO.getSupplierId());

            // Create product using the service method signature
            Product savedProduct = productService.createProduct(
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
                productDTO.getProductName(),
                productDTO.getSize(),
                productDTO.getBrand(),
                productDTO.getPrice(),
                productDTO.getColor(),
                productDTO.getDescription(),
<<<<<<< HEAD
                supplier.orElse(null)
        );

        // Add categories if provided
        if (productDTO.getCategories() != null) {
            productDTO.getCategories().forEach(categoryDTO -> {
                categoryService.findCategoryById(categoryDTO.getId()).ifPresent(category -> 
                    productService.addProductToCategory(newProduct.getId(), category)
                );
            });
        }

        return new ResponseEntity<>(ProductDTO.fromEntity(newProduct), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update product", description = "Updates an existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody ProductDTO productDTO) {
        logger.info("Updating product with ID: {}", id);
        
        Product existingProduct = productService.findProductById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id));

        // Update product fields
        existingProduct = productDTO.updateEntity(existingProduct);
        
        // Update supplier if provided
        if (productDTO.getSupplierId() != null) {
            Supplier supplier = supplierService.findSupplierById(productDTO.getSupplierId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                            "Supplier not found with ID: " + productDTO.getSupplierId()));
            existingProduct.setSupplier(supplier);
        }

        // Save updated product
        Product updatedProduct = productService.updateProduct(existingProduct);
        return ResponseEntity.ok(ProductDTO.fromEntity(updatedProduct));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete product", description = "Deletes a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Integer id) {
        logger.info("Deleting product with ID: {}", id);
        
        if (!productService.findProductById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id);
        }
        
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/price")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update product price", description = "Updates price of a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid price"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductDTO> updateProductPrice(
            @Parameter(description = "Product ID", required = true) @PathVariable Integer id,
            @Parameter(description = "New price", required = true) @RequestParam BigDecimal price) {
        logger.info("Updating price for product ID: {} to {}", id, price);
        
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be negative");
        }
        
        Product updatedProduct = productService.updateProductPrice(id, price);
        return ResponseEntity.ok(ProductDTO.fromEntity(updatedProduct));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update product status", description = "Activates or deactivates a product")
    public ResponseEntity<ProductDTO> updateProductStatus(
            @PathVariable Integer id, 
            @RequestParam Boolean isActive) {
        logger.info("Updating status for product ID: {} to {}", id, isActive);
        
        Product updatedProduct = productService.updateProductStatus(id, isActive);
        return ResponseEntity.ok(ProductDTO.fromEntity(updatedProduct));
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "Get products by brand", description = "Returns products by brand name")
    public ResponseEntity<List<ProductDTO>> getProductsByBrand(@PathVariable String brand) {
        logger.info("Fetching products for brand: {}", brand);
        
        List<Product> products = productService.getProductsByBrand(brand);
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by various criteria")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        logger.info("Searching products with criteria - name: {}, brand: {}, color: {}, size: {}, price range: {} - {}", 
                name, brand, color, size, minPrice, maxPrice);

        List<Product> products = new ArrayList<>();

        // Apply filters based on provided parameters
        if (name != null && !name.isEmpty()) {
            products = productService.getProductsByNameKeyword(name);
        } else if (brand != null && !brand.isEmpty() && color != null && !color.isEmpty()) {
            products = productService.getProductsByBrandAndColor(brand, color);
        } else if (brand != null && !brand.isEmpty() && minPrice != null && maxPrice != null) {
            products = productService.getProductsByBrandAndPriceRange(brand, minPrice, maxPrice);
        } else if (brand != null && !brand.isEmpty()) {
            products = productService.getProductsByBrand(brand);
        } else if (color != null && !color.isEmpty()) {
            products = productService.getProductsByColor(color);
        } else if (size != null && !size.isEmpty()) {
            products = productService.getProductsBySize(size);
        } else if (minPrice != null && maxPrice != null) {
            products = productService.getProductsByPriceRange(minPrice, maxPrice);
        } else if (minPrice != null) {
            products = productService.getProductsByMinPrice(minPrice);
        } else if (maxPrice != null) {
            products = productService.getProductsByMaxPrice(maxPrice);
        } else {
            products = productService.getAllProducts();
        }

        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category", description = "Returns products in a specific category")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Integer categoryId) {
        logger.info("Fetching products for category ID: {}", categoryId);
        
        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + categoryId));
        
        List<Product> products = productService.getProductsByCategory(category);
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest products", description = "Returns the latest products added to the store")
    public ResponseEntity<List<ProductDTO>> getLatestProducts() {
        logger.info("Fetching latest products");
        
        List<Product> products = productService.getLatestProducts();
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/in-stock")
    @Operation(summary = "Get products in stock", description = "Returns products that are currently in stock")
    public ResponseEntity<List<ProductDTO>> getProductsInStock() {
        logger.info("Fetching products in stock");
        
        List<Product> products = productService.getProductsInStock();
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/top-selling")
    @Operation(summary = "Get top selling products", description = "Returns the top selling products")
    public ResponseEntity<List<ProductDTO>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        logger.info("Fetching top {} selling products", limit);
        
        Map<Product, Long> topProducts = productService.getTopSellingProducts(limit);
        List<ProductDTO> productDTOs = topProducts.keySet().stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/launched-after")
    @Operation(summary = "Get products launched after date", description = "Returns products launched after the specified date")
    public ResponseEntity<List<ProductDTO>> getProductsLaunchedAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("Fetching products launched after {}", date);
        
        List<Product> products = productService.getProductsLaunchedAfter(date);
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    @PutMapping("/{productId}/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Add product to category", description = "Adds a product to a category")
    public ResponseEntity<ProductDTO> addProductToCategory(
            @PathVariable Integer productId, 
            @PathVariable Integer categoryId) {
        logger.info("Adding product ID: {} to category ID: {}", productId, categoryId);
        
        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + categoryId));
        
        Product updatedProduct = productService.addProductToCategory(productId, category);
        return ResponseEntity.ok(ProductDTO.fromEntity(updatedProduct));
    }

    @DeleteMapping("/{productId}/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Remove product from category", description = "Removes a product from a category")
    public ResponseEntity<ProductDTO> removeProductFromCategory(
            @PathVariable Integer productId, 
            @PathVariable Integer categoryId) {
        logger.info("Removing product ID: {} from category ID: {}", productId, categoryId);
        
        Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + categoryId));
        
        Product updatedProduct = productService.removeProductFromCategory(productId, category);
        return ResponseEntity.ok(ProductDTO.fromEntity(updatedProduct));
=======
                supplier
            );
            
            return new ResponseEntity<>(ProductDTO.fromEntity(savedProduct), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating product", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating product: " + e.getMessage(), e);
        }
    }

    // Helper method to find supplier
    private Supplier findSupplier(Integer supplierId) {
        if (supplierId == null) {
            return null;
        }
        
        return supplierService.findSupplierById(supplierId)
            .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + supplierId));
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
>>>>>>> 792c76ef0c59203fc34a67fcc0180ab0237bc044
    }
} 