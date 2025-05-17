package com.example.demo.repositories;

import com.example.demo.entities.PhoneNumbersEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneNumbersEmployeeRepository extends JpaRepository<PhoneNumbersEmployee, String> {
    
    // Find all phone numbers for a specific employee
    List<PhoneNumbersEmployee> findByEmployeeId(Integer employeeId);
    
    // Check if a phone number exists for any employee
    boolean existsByPhoneNumber(String phoneNumber);
    
    // Find phone numbers by partial match (for search functionality)
    @Query("SELECT p FROM PhoneNumbersEmployee p WHERE p.phoneNumber LIKE %:searchTerm%")
    List<PhoneNumbersEmployee> searchByPhoneNumber(@Param("searchTerm") String searchTerm);
    
    // Delete all phone numbers for a specific employee
    @Modifying
    @Query("DELETE FROM PhoneNumbersEmployee p WHERE p.employee.id = :employeeId")
    void deleteAllByEmployeeId(@Param("employeeId") Integer employeeId);
    
    // Delete a specific phone number for an employee
    @Modifying
    @Query("DELETE FROM PhoneNumbersEmployee p WHERE p.phoneNumber = :phoneNumber AND p.employee.id = :employeeId")
    void deleteByPhoneNumberAndEmployeeId(@Param("phoneNumber") String phoneNumber, 
                                        @Param("employeeId") Integer employeeId);
    
    // Find phone number by exact match with employee
    Optional<PhoneNumbersEmployee> findByPhoneNumberAndEmployeeId(String phoneNumber, Integer employeeId);
    
    // Check if phone number exists for other employees (for update validation)
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM PhoneNumbersEmployee p " +
           "WHERE p.phoneNumber = :phoneNumber AND p.employee.id != :employeeId")
    boolean existsByPhoneNumberForOtherEmployees(@Param("phoneNumber") String phoneNumber, 
                                              @Param("employeeId") Integer employeeId);

    boolean existsByPhoneNumberAndEmployeeId(String phoneNumber, Integer employeeId);
}