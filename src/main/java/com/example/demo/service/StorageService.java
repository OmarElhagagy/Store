package com.example.demo.service;

import com.example.demo.entities.Employee;
import com.example.demo.entities.Storage;
import com.example.demo.entities.Store;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StorageService {
    
    // Create a new storage location
    Storage createStorage(Store store, String location, Integer reorderLevel, 
                          Integer maxStock, Integer minStock);
    
    // Find storage by ID
    Optional<Storage> findStorageById(Integer id);
    
    // Get all storage locations
    List<Storage> getAllStorageLocations();
    
    // Get storage locations by store
    List<Storage> getStorageByStore(Store store);
    
    // Get storage locations by location keyword
    List<Storage> getStorageByLocationKeyword(String locationKeyword);
    
    // Get storage locations by store and location keyword
    List<Storage> getStorageByStoreAndLocationKeyword(Store store, String locationKeyword);
    
    // Update storage details
    Storage updateStorage(Storage storage);
    
    // Update storage stock levels
    Storage updateStockLevels(Integer storageId, Integer reorderLevel, 
                             Integer maxStock, Integer minStock);
    
    // Assign employee to storage
    void assignEmployeeToStorage(Storage storage, Employee employee);
    
    // Remove employee from storage
    void removeEmployeeFromStorage(Storage storage, Employee employee);
    
    // Get all employees assigned to storage
    Set<Employee> getEmployeesAssignedToStorage(Integer storageId);
    
    // Get storage locations that need reordering
    List<Storage> getStorageNeedingReorder();
    
    // Get storage locations at capacity
    List<Storage> getStorageAtCapacity();
    
    // Get storage locations with low stock
    List<Storage> getStorageWithLowStock();
    
    // Delete storage
    void deleteStorage(Integer storageId);
}