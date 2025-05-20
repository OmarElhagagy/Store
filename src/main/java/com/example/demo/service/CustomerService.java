package com.example.demo.service;

import com.example.demo.entities.Customer;
import com.example.demo.entities.CustomerOrder;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface CustomerService {
    List<Customer> findAll();
    Optional<Customer> findById(Integer id);
    Optional<Customer> findByEmail(String email);
    List<Customer> findByLastName(String lastName);
    List<Customer> findByFirstName(String firstName);
    List<Customer> findByBirthDateBefore(LocalDate date);
    List<Customer> findByBirthDateRange(LocalDate startDate, LocalDate endDate);
    List<Customer> findByGender(String gender);
    Customer save(Customer customer);
    Customer update(Customer customer);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    boolean existsByEmail(String email);
    Customer restoreCustomer(Integer id);
    List<Customer> findInactiveCustomers();
    long count();

    List<CustomerOrder> getCustomerOrders(Integer customerId);
    
    // Create a new customer with basic information
    Customer createCustomer(String firstName, String lastName, String email);
}
