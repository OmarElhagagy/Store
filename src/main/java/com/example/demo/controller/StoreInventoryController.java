package com.example.demo.controller;

import com.example.demo.dto.StoreInventoryDTO;
import com.example.demo.entities.Product;
import com.example.demo.entities.Store;
import com.example.demo.entities.StoreInventory;
import com.example.demo.entities.StoreInventoryId;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.ProductService;
import com.example.demo.service.StoreInventoryService;
import com.example.demo.service.StoreService;
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
@RequestMapping("/api/v1/inventory")
@Tag(name = "Store Inventory Controller", description = "API to manage store inventory")
public class StoreInventoryController {

    private static final Logger logger = LoggerFactory.getLogger(StoreInventoryController.class);

    private final StoreInventoryService storeInventoryService;
    private final StoreService storeService;
    private final ProductService productService;

    @Autowired
    public StoreInventoryController(StoreInventoryService storeInventoryService, 
                                   StoreService storeService, 
                                   ProductService productService) {
        this.storeInventoryService = storeInventoryService;
        this.storeService = storeService;
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get all inventory", description = "Returns a list of all inventory records")
    public ResponseEntity<List<StoreInventoryDTO>> getAllInventory() {
        logger.info("Fetching all inventory records");
        List<StoreInventory> inventoryList = storeInventoryService.getAllInventory();
        List<StoreInventoryDTO> inventoryDTOs = inventoryList.stream()
                .map(StoreInventoryDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(inventoryDTOs);
    }

    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get inventory by store", description = "Returns inventory for a specific store")
    public ResponseEntity<List<StoreInventoryDTO>> getInventoryByStore(@PathVariable Integer storeId) {
        logger.info("Fetching inventory for store ID: {}", storeId);
        
        Store store = storeService.findStoreById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));
        
        List<StoreInventory> inventoryList = storeInventoryService.getInventoryByStore(store);
        List<StoreInventoryDTO> inventoryDTOs = inventoryList.stream()
                .map(StoreInventoryDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(inventoryDTOs);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory by product", description = "Returns inventory records for a specific product across all stores")
    public ResponseEntity<List<StoreInventoryDTO>> getInventoryByProduct(@PathVariable Integer productId) {
        logger.info("Fetching inventory for product ID: {}", productId);
        
        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        List<StoreInventory> inventoryList = storeInventoryService.getInventoryByProduct(product);
        List<StoreInventoryDTO> inventoryDTOs = inventoryList.stream()
                .map(StoreInventoryDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(inventoryDTOs);
    }

    @GetMapping("/store/{storeId}/product/{productId}")
    @Operation(summary = "Get inventory by store and product", description = "Returns inventory record for a specific product in a specific store")
    public ResponseEntity<StoreInventoryDTO> getInventoryByStoreAndProduct(
            @PathVariable Integer storeId, 
            @PathVariable Integer productId) {
        logger.info("Fetching inventory for store ID: {} and product ID: {}", storeId, productId);
        
        Store store = storeService.findStoreById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));
        
        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        StoreInventoryId id = new StoreInventoryId(store.getId(), product.getId());
        
        StoreInventory inventory = storeInventoryService.findInventoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "storeId and productId", storeId + ", " + productId));
        
        return ResponseEntity.ok(StoreInventoryDTO.fromEntity(inventory));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get low stock inventory", description = "Returns inventory records with stock below threshold")
    public ResponseEntity<List<StoreInventoryDTO>> getLowStockInventory(
            @RequestParam(defaultValue = "10") Integer threshold) {
        logger.info("Fetching low stock inventory with threshold: {}", threshold);
        
        List<StoreInventory> inventoryList = storeInventoryService.getLowStockInventory(threshold);
        List<StoreInventoryDTO> inventoryDTOs = inventoryList.stream()
                .map(StoreInventoryDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(inventoryDTOs);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Create new inventory record", description = "Creates a new inventory record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventory record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Store or product not found"),
            @ApiResponse(responseCode = "409", description = "Inventory record already exists")
    })
    public ResponseEntity<StoreInventoryDTO> createInventory(@Valid @RequestBody StoreInventoryDTO inventoryDTO) {
        logger.info("Creating new inventory for store ID: {} and product ID: {}", 
                inventoryDTO.getStoreId(), inventoryDTO.getProductId());
        
        Store store = storeService.findStoreById(inventoryDTO.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", inventoryDTO.getStoreId()));
        
        Product product = productService.findProductById(inventoryDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", inventoryDTO.getProductId()));
        
        StoreInventoryId id = new StoreInventoryId(store.getId(), product.getId());
        
        // Check if inventory record already exists
        if (storeInventoryService.findInventoryById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        StoreInventory inventory = storeInventoryService.createInventory(
                store,
                product,
                inventoryDTO.getQuantity(),
                inventoryDTO.getLocation()
        );
        
        return new ResponseEntity<>(StoreInventoryDTO.fromEntity(inventory), HttpStatus.CREATED);
    }

    @PutMapping("/store/{storeId}/product/{productId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update inventory", description = "Updates an existing inventory record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Inventory record not found")
    })
    public ResponseEntity<StoreInventoryDTO> updateInventory(
            @PathVariable Integer storeId,
            @PathVariable Integer productId,
            @Valid @RequestBody StoreInventoryDTO inventoryDTO) {
        logger.info("Updating inventory for store ID: {} and product ID: {}", storeId, productId);
        
        StoreInventoryId id = new StoreInventoryId(storeId, productId);
        
        StoreInventory existingInventory = storeInventoryService.findInventoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "storeId and productId", storeId + ", " + productId));
        
        existingInventory.setQuantity(inventoryDTO.getQuantity());
        existingInventory.setLocation(inventoryDTO.getLocation());
        
        StoreInventory updatedInventory = storeInventoryService.updateInventory(existingInventory);
        return ResponseEntity.ok(StoreInventoryDTO.fromEntity(updatedInventory));
    }

    @DeleteMapping("/store/{storeId}/product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete inventory", description = "Deletes an inventory record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inventory deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory record not found")
    })
    public ResponseEntity<Void> deleteInventory(
            @PathVariable Integer storeId,
            @PathVariable Integer productId) {
        logger.info("Deleting inventory for store ID: {} and product ID: {}", storeId, productId);
        
        StoreInventoryId id = new StoreInventoryId(storeId, productId);
        
        if (!storeInventoryService.findInventoryById(id).isPresent()) {
            throw new ResourceNotFoundException("Inventory", "storeId and productId", storeId + ", " + productId);
        }
        
        storeInventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/store/{storeId}/product/{productId}/adjust")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Adjust inventory quantity", description = "Adjusts the quantity of an inventory record")
    public ResponseEntity<StoreInventoryDTO> adjustInventoryQuantity(
            @PathVariable Integer storeId,
            @PathVariable Integer productId,
            @RequestParam Integer adjustment) {
        logger.info("Adjusting inventory quantity for store ID: {} and product ID: {} by {}", 
                storeId, productId, adjustment);
        
        StoreInventoryId id = new StoreInventoryId(storeId, productId);
        
        StoreInventory inventory = storeInventoryService.findInventoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "storeId and productId", storeId + ", " + productId));
        
        StoreInventory updatedInventory = storeInventoryService.adjustInventoryQuantity(id, adjustment);
        return ResponseEntity.ok(StoreInventoryDTO.fromEntity(updatedInventory));
    }
} 