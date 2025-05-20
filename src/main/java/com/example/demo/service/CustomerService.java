package com.example.demo.service;

import com.example.demo.entities.Customer;
import com.example.demo.entities.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface CustomerService {
    List<Customer> findAll();
    Page<Customer> findAllWithPagination(Pageable pageable);
    Optional<Customer> findById(Integer id);
    Optional<Customer> findByEmail(String email);
    List<Customer> findByLastName(String lastName);
    List<Customer> findByFirstName(String firstName);
    List<Customer> findByBirthDateBefore(LocalDate date);
    List<Customer> findByBirthDateRange(LocalDate startDate, LocalDate endDate);
    List<Customer> findByGender(String gender);
    Customer save(Customer customer);
    Customer createCustomer(Customer customer);
    Customer update(Customer customer);
    Customer updateCustomer(Customer customer);
    void deleteById(Integer id);
    void deleteCustomer(Integer id);
    Customer activateCustomer(Integer id);
    Customer deactivateCustomer(Integer id);
    boolean existsById(Integer id);
    boolean existsByEmail(String email);
    Customer restoreCustomer(Integer id);
    List<Customer> findInactiveCustomers();
    long count();

    List<CustomerOrder> getCustomerOrders(Integer customerId);
    
    // Create a new customer with basic information
    Customer createCustomer(String firstName, String lastName, String email);
}
