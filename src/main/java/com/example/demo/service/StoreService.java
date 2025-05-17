package com.example.demo.service;

import com.example.demo.entities.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StoreService {
    
    // Create a new store
    Store createStore(String address, String email, Integer minStock);
    
    // Find store by ID
    Optional<Store> findStoreById(Integer id);
    
    // Get all stores
    List<Store> getAllStores();
    
    // Get stores by address keyword
    List<Store> getStoresByAddressKeyword(String addressKeyword);
    
    // Get stores by email keyword
    List<Store> getStoresByEmailKeyword(String emailKeyword);
    
    // Update store details
    Store updateStore(Store store);
    
    // Update store address and email
    Store updateStoreContactInfo(Integer storeId, String address, String email);
    
    // Update store minimum stock level
    Store updateStoreMinStock(Integer storeId, Integer minStock);
    
    // Get all employees for a store
    Set<Employee> getStoreEmployees(Integer storeId);
    
    // Get all storage locations for a store
    Set<Storage> getStoreStorageLocations(Integer storeId);
    
    // Get all inventory for a store
    Set<StoreInventory> getStoreInventory(Integer storeId);
    
    // Get all notifications for a store
    Set<Notification> getStoreNotifications(Integer storeId);
    
    // Get all reserved stock for a store
    Set<ReservedStock> getStoreReservedStock(Integer storeId);
    
    // Get stores with low inventory
    List<Store> getStoresWithLowInventory();
    
    // Get stores with storage locations
    List<Store> getStoresWithStorageLocations();
    
    // Get stores with no employees
    List<Store> getStoresWithNoEmployees();
    
    // Get stores with reserved stock
    List<Store> getStoresWithReservedStock();
    
    // Get stores with unread notifications
    List<Store> getStoresWithUnreadNotifications();
    
    // Delete store
    void deleteStore(Integer storeId);
}