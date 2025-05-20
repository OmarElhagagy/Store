package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.SupplierDTO;
import com.example.demo.entities.Product;
import com.example.demo.entities.Supplier;
import com.example.demo.exception.ResourceNotFoundException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/suppliers")
@Tag(name = "Supplier Controller", description = "API to manage product suppliers")
public class SupplierController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

    private final SupplierService supplierService;
    private final ProductService productService;

    @Autowired
    public SupplierController(SupplierService supplierService, ProductService productService) {
        this.supplierService = supplierService;
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get all suppliers", description = "Returns a list of all suppliers")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        logger.info("Fetching all suppliers");
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        List<SupplierDTO> supplierDTOs = suppliers.stream()
                .map(SupplierDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(supplierDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get supplier by ID", description = "Returns a single supplier by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier found",
                    content = @Content(schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    public ResponseEntity<SupplierDTO> getSupplierById(
            @Parameter(description = "Supplier ID", required = true) @PathVariable Integer id) {
        logger.info("Fetching supplier with ID: {}", id);
        
        Supplier supplier = supplierService.findSupplierById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        
        return ResponseEntity.ok(SupplierDTO.fromEntity(supplier));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new supplier", description = "Creates a new supplier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supplier created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Supplier already exists")
    })
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        logger.info("Creating new supplier: {}", supplierDTO.getSupplierName());
        
        // Check if supplier already exists with the same name
        if (supplierService.existsByName(supplierDTO.getSupplierName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Supplier newSupplier = supplierService.createSupplier(
                supplierDTO.getSupplierName(),
                supplierDTO.getContactName(),
                supplierDTO.getPhone(),
                supplierDTO.getEmail(),
                supplierDTO.getWebsite()
        );
        
        return new ResponseEntity<>(SupplierDTO.fromEntity(newSupplier), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update supplier", description = "Updates an existing supplier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "409", description = "Supplier name already exists")
    })
    public ResponseEntity<SupplierDTO> updateSupplier(
            @Parameter(description = "Supplier ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody SupplierDTO supplierDTO) {
        logger.info("Updating supplier with ID: {}", id);
        
        Supplier existingSupplier = supplierService.findSupplierById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        
        // Check if supplier name already exists (excluding the current supplier)
        if (!existingSupplier.getSupplierName().equals(supplierDTO.getSupplierName()) && 
                supplierService.existsByName(supplierDTO.getSupplierName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        existingSupplier.setSupplierName(supplierDTO.getSupplierName());
        existingSupplier.setContactName(supplierDTO.getContactName());
        existingSupplier.setPhone(supplierDTO.getPhone());
        existingSupplier.setEmail(supplierDTO.getEmail());
        existingSupplier.setWebsite(supplierDTO.getWebsite());
        
        Supplier updatedSupplier = supplierService.updateSupplier(existingSupplier);
        return ResponseEntity.ok(SupplierDTO.fromEntity(updatedSupplier));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete supplier", description = "Deletes a supplier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supplier deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "409", description = "Supplier has associated products")
    })
    public ResponseEntity<Void> deleteSupplier(
            @Parameter(description = "Supplier ID", required = true) @PathVariable Integer id) {
        logger.info("Deleting supplier with ID: {}", id);
        
        Supplier supplier = supplierService.findSupplierById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        
        // Check if supplier has associated products
        if (supplierService.hasProducts(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Search suppliers", description = "Search suppliers by name")
    public ResponseEntity<List<SupplierDTO>> searchSuppliers(@RequestParam String name) {
        logger.info("Searching suppliers with name containing: {}", name);
        
        List<Supplier> suppliers = supplierService.searchSuppliersByName(name);
        List<SupplierDTO> supplierDTOs = suppliers.stream()
                .map(SupplierDTO::fromEntity)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(supplierDTOs);
    }
    
    @GetMapping("/{id}/products")
    @Operation(summary = "Get supplier products", description = "Returns products from a specific supplier")
    public ResponseEntity<List<ProductDTO>> getSupplierProducts(@PathVariable Integer id) {
        logger.info("Fetching products for supplier ID: {}", id);
        
        Supplier supplier = supplierService.findSupplierById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        
        List<Product> products = productService.getProductsBySupplier(supplier);
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(productDTOs);
    }
} 