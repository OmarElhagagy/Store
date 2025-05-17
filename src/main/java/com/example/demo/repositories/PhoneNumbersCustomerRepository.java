package com.example.demo.repositories;

import com.example.demo.entities.PhoneNumbersCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneNumbersCustomerRepository extends JpaRepository<PhoneNumbersCustomer, String> {
    
    // Find all phone numbers for a specific customer
    List<PhoneNumbersCustomer> findByCustomerId(Integer customerId);
    
    // Check if a phone number exists for any customer
    boolean existsByPhoneNumber(String phoneNumber);
    
    // Find phone numbers by partial match (for search functionality)
    @Query("SELECT p FROM PhoneNumbersCustomer p WHERE p.phoneNumber LIKE %:searchTerm%")
    List<PhoneNumbersCustomer> searchByPhoneNumber(@Param("searchTerm") String searchTerm);
    
    // Delete all phone numbers for a specific customer
    @Modifying
    @Query("DELETE FROM PhoneNumbersCustomer p WHERE p.customer.id = :customerId")
    void deleteAllByCustomerId(@Param("customerId") Integer customerId);
    
    // Delete a specific phone number for a customer
    @Modifying
    @Query("DELETE FROM PhoneNumbersCustomer p WHERE p.phoneNumber = :phoneNumber AND p.customer.id = :customerId")
    void deleteByPhoneNumberAndCustomerId(@Param("phoneNumber") String phoneNumber, 
                                        @Param("customerId") Integer customerId);
    
    // Find phone number by exact match with customer
    Optional<PhoneNumbersCustomer> findByPhoneNumberAndCustomerId(String phoneNumber, Integer customerId);

    boolean existsByPhoneNumberAndCustomerId(String phoneNumber, Integer customerId);
}