package com.example.demo.service;

import com.example.demo.entities.PhoneNumbersSupplier;
import java.util.List;

public interface PhoneNumbersSupplierService {
    
    // Add a new phone number for a supplier
    PhoneNumbersSupplier addPhoneNumber(Integer supplierId, String phoneNumber);
    
    // Get all phone numbers for a supplier
    List<PhoneNumbersSupplier> getPhoneNumbersBySupplier(Integer supplierId);
    
    // Delete a specific phone number
    void deletePhoneNumber(String phoneNumber, Integer supplierId);
    
    // Delete all phone numbers for a supplier
    void deleteAllPhoneNumbersForSupplier(Integer supplierId);
    
    // Check if a phone number exists in the system
    boolean phoneNumberExists(String phoneNumber);
    
    // Search phone numbers by partial match
    List<PhoneNumbersSupplier> searchPhoneNumbers(String searchTerm);
    
    // Get a specific phone number entry
    PhoneNumbersSupplier getPhoneNumber(String phoneNumber, Integer supplierId);
    
    // Check if phone number exists for other suppliers
    boolean phoneNumberExistsForOtherSuppliers(String phoneNumber, Integer supplierId);
}