package com.example.demo.controller;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.entities.Customer;
import com.example.demo.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for managing customers
 */
@RestController
@RequestMapping("/api/customers")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Get all customers with pagination
     * Restricted to admin users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(Pageable pageable) {
        log.info("REST request to get all Customers with pagination");
        Page<Customer> customers = customerService.findAllWithPagination(pageable);
        Page<CustomerDTO> customerDTOs = customers.map(CustomerDTO::fromEntity);
        return ResponseEntity.ok(customerDTOs);
    }

    /**
     * Get a specific customer by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#id)")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Integer id) {
        log.info("REST request to get Customer with ID {}", id);
        Optional<Customer> customer = customerService.findById(id);
        return customer
                .map(value -> ResponseEntity.ok(CustomerDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Search customers by email
     * Restricted to admin users
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDTO> searchCustomerByEmail(@RequestParam String email) {
        log.info("REST request to find Customer with email {}", email);
        Optional<Customer> customer = customerService.findByEmail(email);
        return customer
                .map(value -> ResponseEntity.ok(CustomerDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new customer
     * Public endpoint for registration
     */
    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerDTO customerDTO, BindingResult bindingResult) {
        log.info("REST request to create a new Customer");
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            // Check if email is already in use
            if (customerService.findByEmail(customerDTO.getEmail()).isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("email", "Email is already in use");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            Customer customer = new Customer();
            customerDTO.updateEntity(customer);
            
            Customer savedCustomer = customerService.createCustomer(customer);
            
            return new ResponseEntity<>(CustomerDTO.fromEntity(savedCustomer), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating customer", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating customer: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing customer
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#id)")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Integer id,
            @Valid @RequestBody CustomerDTO customerDTO,
            BindingResult bindingResult) {
        log.info("REST request to update Customer with ID {}", id);
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            // Find existing customer
            Customer existingCustomer = customerService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID " + id));
            
            // Check if trying to update email to one that already exists
            if (!existingCustomer.getEmail().equals(customerDTO.getEmail()) &&
                    customerService.findByEmail(customerDTO.getEmail()).isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("email", "Email is already in use");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
            
            customerDTO.updateEntity(existingCustomer);
            
            Customer updatedCustomer = customerService.updateCustomer(existingCustomer);
            
            return ResponseEntity.ok(CustomerDTO.fromEntity(updatedCustomer));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error updating customer", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating customer: " + e.getMessage(), e);
        }
    }

    /**
     * Delete a customer
     * Restricted to admin users or the customer themselves
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#id)")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        log.info("REST request to delete Customer with ID {}", id);
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting customer", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting customer: " + e.getMessage(), e);
        }
    }

    /**
     * Activate a customer account
     * Restricted to admin users
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDTO> activateCustomer(@PathVariable Integer id) {
        log.info("REST request to activate Customer with ID {}", id);
        try {
            Customer customer = customerService.activateCustomer(id);
            return ResponseEntity.ok(CustomerDTO.fromEntity(customer));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error activating customer", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error activating customer: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivate a customer account
     * Restricted to admin users
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerDTO> deactivateCustomer(@PathVariable Integer id) {
        log.info("REST request to deactivate Customer with ID {}", id);
        try {
            Customer customer = customerService.deactivateCustomer(id);
            return ResponseEntity.ok(CustomerDTO.fromEntity(customer));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error deactivating customer", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deactivating customer: " + e.getMessage(), e);
        }
    }

    /**
     * Handle validation errors and return appropriate response
     */
    private ResponseEntity<Map<String, String>> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
} 