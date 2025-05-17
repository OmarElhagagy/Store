package com.example.demo.service;

import com.example.demo.entities.PhoneNumbersSupplier;
import com.example.demo.entities.Product;
import com.example.demo.entities.Supplier;
import com.example.demo.entities.SupplyOrder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SupplierService {
    
    // Create a new supplier
    Supplier createSupplier(String supplierName, String email);
    
    // Find supplier by ID
    Optional<Supplier> findSupplierById(Integer id);
    
    // Get all suppliers
    List<Supplier> getAllSuppliers();
    
    // Get suppliers by name keyword
    List<Supplier> getSuppliersByNameKeyword(String supplierNameKeyword);
    
    // Get suppliers by email keyword
    List<Supplier> getSuppliersByEmailKeyword(String emailKeyword);
    
    // Update supplier information
    Supplier updateSupplier(Supplier supplier);
    
    // Update supplier name and email
    Supplier updateSupplierInfo(Integer supplierId, String supplierName, String email);
    
    // Get phone numbers for a supplier
    Set<PhoneNumbersSupplier> getSupplierPhoneNumbers(Integer supplierId);
    
    // Get products from a supplier
    Set<Product> getSupplierProducts(Integer supplierId);
    
    // Get supply orders for a supplier
    Set<SupplyOrder> getSupplierOrders(Integer supplierId);
    
    // Find suppliers with products
    List<Supplier> getSuppliersWithProducts();
    
    // Find suppliers with active orders
    List<Supplier> getSuppliersWithActiveOrders();
    
    // Find suppliers without phone numbers
    List<Supplier> getSuppliersWithoutPhoneNumbers();
    
    // Find suppliers with supply orders
    List<Supplier> getSuppliersWithSupplyOrders();
    
    // Get supplier count by name pattern
    long getSupplierCountByNamePattern(String supplierNameKeyword);
    
    // Delete supplier
    void deleteSupplier(Integer supplierId);
}