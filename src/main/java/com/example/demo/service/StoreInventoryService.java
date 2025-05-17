package com.example.demo.service;

import com.example.demo.entities.Product;
import com.example.demo.entities.Store;
import com.example.demo.entities.StoreInventory;
import com.example.demo.entities.StoreInventoryId;

import java.util.List;
import java.util.Optional;

public interface StoreInventoryService {
    
    // Create a new inventory record
    StoreInventory createInventory(Store store, Product product, Integer quantity);
    
    // Find inventory by composite ID
    Optional<StoreInventory> findInventoryById(StoreInventoryId id);
    
    // Find inventory by store and product
    Optional<StoreInventory> findInventoryByStoreAndProduct(Store store, Product product);
    
    // Get all inventory items
    List<StoreInventory> getAllInventoryItems();
    
    // Get all inventory for a specific store
    List<StoreInventory> getInventoryByStore(Store store);
    
    // Get all stores that have a specific product
    List<StoreInventory> getInventoryByProduct(Product product);
    
    // Update inventory quantity
    StoreInventory updateInventoryQuantity(Store store, Product product, Integer newQuantity);
    
    // Increment inventory quantity
    StoreInventory incrementInventoryQuantity(Store store, Product product, Integer incrementAmount);
    
    // Decrement inventory quantity
    StoreInventory decrementInventoryQuantity(Store store, Product product, Integer decrementAmount);
    
    // Get low stock items (quantity below store's minimum)
    List<StoreInventory> getLowStockItems();
    
    // Get low stock items for a specific store
    List<StoreInventory> getLowStockItemsByStore(Store store);
    
    // Get out of stock items
    List<StoreInventory> getOutOfStockItems();
    
    // Get items with quantity less than or equal to specified amount
    List<StoreInventory> getItemsWithQuantityLessThanEqual(Integer maxQuantity);
    
    // Get items with quantity greater than or equal to specified amount
    List<StoreInventory> getItemsWithQuantityGreaterThanEqual(Integer minQuantity);
    
    // Delete inventory record
    void deleteInventory(StoreInventoryId id);
    
    // Delete inventory record by store and product
    void deleteInventoryByStoreAndProduct(Store store, Product product);
}