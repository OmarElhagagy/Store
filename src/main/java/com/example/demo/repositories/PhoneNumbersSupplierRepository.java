package com.example.demo.repositories;

import com.example.demo.entities.PhoneNumbersSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneNumbersSupplierRepository extends JpaRepository<PhoneNumbersSupplier, String> {

    // Find all phone numbers for a specific supplier
    List<PhoneNumbersSupplier> findBySupplierId(Integer supplierId);
    
    // Check if a phone number exists in the system
    boolean existsByPhoneNumber(String phoneNumber);
    
    // Find phone numbers by partial match (search functionality)
    @Query("SELECT p FROM PhoneNumbersSupplier p WHERE p.phoneNumber LIKE %:searchTerm%")
    List<PhoneNumbersSupplier> searchByPhoneNumber(@Param("searchTerm") String searchTerm);
    
    // Delete all phone numbers for a specific supplier
    @Modifying
    @Query("DELETE FROM PhoneNumbersSupplier p WHERE p.supplier.id = :supplierId")
    void deleteAllBySupplierId(@Param("supplierId") Integer supplierId);
    
    // Delete a specific phone number for a supplier
    @Modifying
    @Query("DELETE FROM PhoneNumbersSupplier p WHERE p.phoneNumber = :phoneNumber AND p.supplier.id = :supplierId")
    void deleteByPhoneNumberAndSupplierId(@Param("phoneNumber") String phoneNumber, 
                                        @Param("supplierId") Integer supplierId);
    
    // Find phone number by exact match with supplier
    Optional<PhoneNumbersSupplier> findByPhoneNumberAndSupplierId(String phoneNumber, Integer supplierId);
    
    // Check if phone number exists for other suppliers
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM PhoneNumbersSupplier p " +
           "WHERE p.phoneNumber = :phoneNumber AND p.supplier.id != :supplierId")
    boolean existsByPhoneNumberForOtherSuppliers(@Param("phoneNumber") String phoneNumber, 
                                               @Param("supplierId") Integer supplierId);

    boolean existsByPhoneNumberAndSupplierId(String phoneNumber, Integer supplierId);
}