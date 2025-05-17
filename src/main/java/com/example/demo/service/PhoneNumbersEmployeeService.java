package com.example.demo.service;

import com.example.demo.entities.PhoneNumbersEmployee;
import java.util.List;

public interface PhoneNumbersEmployeeService {
    
    // Add a new phone number for an employee
    PhoneNumbersEmployee addPhoneNumber(Integer employeeId, String phoneNumber);
    
    // Get all phone numbers for an employee
    List<PhoneNumbersEmployee> getPhoneNumbersByEmployee(Integer employeeId);
    
    // Delete a specific phone number
    void deletePhoneNumber(String phoneNumber, Integer employeeId);
    
    // Delete all phone numbers for an employee
    void deleteAllPhoneNumbersForEmployee(Integer employeeId);
    
    // Check if a phone number exists in the system
    boolean phoneNumberExists(String phoneNumber);
    
    // Search phone numbers by partial match
    List<PhoneNumbersEmployee> searchPhoneNumbers(String searchTerm);
    
    // Get a specific phone number entry
    PhoneNumbersEmployee getPhoneNumber(String phoneNumber, Integer employeeId);
    
    // Check if phone number exists for other employees
    boolean phoneNumberExistsForOtherEmployees(String phoneNumber, Integer employeeId);
}