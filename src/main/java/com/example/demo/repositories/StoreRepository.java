package com.example.demo.repositories;

import com.example.demo.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {
    
    // Find stores by address
    List<Store> findByAddressContaining(String addressKeyword);
    
    // Find stores by email
    List<Store> findByEmailContaining(String emailKeyword);
    
    // Find stores with inventory below minimum stock level
    @Query("SELECT DISTINCT s FROM Store s JOIN s.storeInventories si WHERE si.quantity <= s.minStock")
    List<Store> findStoresWithLowInventory();
    
    // Find stores with at least one storage location
    @Query("SELECT DISTINCT s FROM Store s WHERE SIZE(s.storages) > 0")
    List<Store> findStoresWithStorageLocations();
    
    // Find stores with no employees
    @Query("SELECT s FROM Store s WHERE SIZE(s.employees) = 0")
    List<Store> findStoresWithNoEmployees();
    
    // Find stores with reserved stock
    @Query("SELECT DISTINCT s FROM Store s WHERE SIZE(s.reservedStocks) > 0")
    List<Store> findStoresWithReservedStock();
    
    // Find stores with unread notifications
    @Query("SELECT DISTINCT s FROM Store s JOIN s.notifications n WHERE n.isRead = false")
    List<Store> findStoresWithUnreadNotifications();
}