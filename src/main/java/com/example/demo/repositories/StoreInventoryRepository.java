package com.example.demo.repositories;

import com.example.demo.entities.Product;
import com.example.demo.entities.Store;
import com.example.demo.entities.StoreInventory;
import com.example.demo.entities.StoreInventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreInventoryRepository extends JpaRepository<StoreInventory, StoreInventoryId> {
    
    // Find all inventory items for a specific store
    List<StoreInventory> findByStore(Store store);
    
    // Find all stores that have a specific product
    List<StoreInventory> findByProduct(Product product);
    
    // Find specific inventory item by store and product
    Optional<StoreInventory> findByStoreAndProduct(Store store, Product product);
    
    // Find low stock items (where quantity is below store's min stock level)
    @Query("SELECT si FROM StoreInventory si WHERE si.quantity <= si.store.minStock")
    List<StoreInventory> findLowStockItems();
    
    // Find out of stock items
    List<StoreInventory> findByQuantity(Integer quantity);
    
    // Find items with quantity less than or equal to a specified amount
    List<StoreInventory> findByQuantityLessThanEqual(Integer maxQuantity);
    
    // Find items with quantity greater than or equal to a specified amount
    List<StoreInventory> findByQuantityGreaterThanEqual(Integer minQuantity);
    
    // Find low stock items for a specific store
    @Query("SELECT si FROM StoreInventory si WHERE si.store = ?1 AND si.quantity <= si.store.minStock")
    List<StoreInventory> findLowStockItemsByStore(Store store);

    Optional<StoreInventory> findByStoreIdAndProductId(Integer storeId, Integer productId);
}