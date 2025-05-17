package com.example.demo.repositories;

import com.example.demo.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    
    // Find by supplier name
    List<Supplier> findBySupplierNameContaining(String supplierNameKeyword);
    
    // Find by email
    List<Supplier> findByEmailContaining(String emailKeyword);
    
    // Find suppliers with products
    @Query("SELECT DISTINCT s FROM Supplier s WHERE SIZE(s.products) > 0")
    List<Supplier> findSuppliersWithProducts();
    
    // Find suppliers with active supply orders
    @Query("SELECT DISTINCT s FROM Supplier s JOIN s.supplyOrders so WHERE so.status = 'PENDING' OR so.status = 'PROCESSING'")
    List<Supplier> findSuppliersWithActiveOrders();
    
    // Find suppliers without phone numbers
    @Query("SELECT s FROM Supplier s WHERE SIZE(s.phoneNumbersSuppliers) = 0")
    List<Supplier> findSuppliersWithoutPhoneNumbers();
    
    // Find suppliers with supply orders
    @Query("SELECT DISTINCT s FROM Supplier s WHERE SIZE(s.supplyOrders) > 0")
    List<Supplier> findSuppliersWithSupplyOrders();
    
    // Count suppliers by name pattern
    long countBySupplierNameContaining(String supplierNameKeyword);
}