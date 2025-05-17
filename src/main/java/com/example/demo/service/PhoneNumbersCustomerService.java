package com.example.demo.service;

import com.example.demo.entities.PhoneNumbersCustomer;
import java.util.List;

public interface PhoneNumbersCustomerService {
    
    // Add a new phone number for a customer
    PhoneNumbersCustomer addPhoneNumber(Integer customerId, String phoneNumber);
    
    // Get all phone numbers for a customer
    List<PhoneNumbersCustomer> getPhoneNumbersByCustomer(Integer customerId);
    
    // Delete a specific phone number
    void deletePhoneNumber(String phoneNumber, Integer customerId);
    
    // Delete all phone numbers for a customer
    void deleteAllPhoneNumbersForCustomer(Integer customerId);
    
    // Check if a phone number exists
    boolean phoneNumberExists(String phoneNumber);
    
    // Search phone numbers by partial match
    List<PhoneNumbersCustomer> searchPhoneNumbers(String searchTerm);
    
    // Get a specific phone number entry
    PhoneNumbersCustomer getPhoneNumber(String phoneNumber, Integer customerId);
}