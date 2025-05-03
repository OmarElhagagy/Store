package com.example.demo.repositories;

import com.example.demo.entities.Customer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // Custom finders for active customers
    @Query("SELECT c FROM Customer c WHERE c.email = :email AND c.active = true")
    Optional<Customer> findByEmail(@Param("email") String email);
    
    @Query("SELECT c FROM Customer c WHERE LOWER(c.fName) LIKE LOWER(CONCAT('%', :name, '%')) AND c.active = true")
    List<Customer> findByFNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT c FROM Customer c WHERE LOWER(c.lName) LIKE LOWER(CONCAT('%', :name, '%')) AND c.active = true")
    List<Customer> findByLNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT c FROM Customer c WHERE c.birthDate < :date AND c.active = true")
    List<Customer> findByBirthDateBefore(@Param("date") LocalDate date);
    
    @Query("SELECT c FROM Customer c WHERE c.birthDate BETWEEN :startDate AND :endDate AND c.active = true")
    List<Customer> findByBirthDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c FROM Customer c WHERE c.gender = :gender AND c.active = true")
    List<Customer> findByGender(@Param("gender") String gender);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c WHERE c.email = :email AND c.active = true")
    boolean existsByEmail(@Param("email") String email);
    
    // Override JPA repository default methods to filter by active status
    @Override
    @Query("SELECT c FROM Customer c WHERE c.active = true")
    List<Customer> findAll();
    
    @Override
    @Query("SELECT c FROM Customer c WHERE c.id = :id AND c.active = true")
    Optional<Customer> findById(@Param("id") Integer id);
    
    @Override
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.active = true")
    long count();
    
    // For admin/recovery purposes - can find inactive customers too
    @Query("SELECT c FROM Customer c WHERE c.id = :id")
    Optional<Customer> findCustomerById(@Param("id") Integer id);
    
    @Query("SELECT c FROM Customer c WHERE c.active = false")
    List<Customer> findInactiveCustomers();
}
