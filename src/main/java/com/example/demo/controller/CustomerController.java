package com.example.demo.controller;

import com.example.demo.dto.CustomerDTO;
import com.example.demo.entities.Customer;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Controller", description = "API to manage customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get all customers", description = "Returns a list of all customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers found",
                    content = @Content(schema = @Schema(implementation = CustomerDTO.class)))
    })
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        logger.info("Fetching all customers");
        List<Customer> customers = customerService.findAll();
        List<CustomerDTO> customerDTOs = customers.stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(customerDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or @customerAuthorizationService.isCustomerAuthorized(authentication, #id)")
    @Operation(summary = "Get customer by ID", description = "Returns a single customer by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(schema = @Schema(implementation = CustomerDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "Customer ID", required = true) @PathVariable Integer id) {
        logger.info("Fetching customer with ID: {}", id);
        
        Customer customer = customerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        return ResponseEntity.ok(CustomerDTO.fromEntity(customer));
    }

    @PostMapping
    @Operation(summary = "Create new customer", description = "Creates a new customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        logger.info("Creating new customer: {}", customerDTO);
        
        // Check if email already exists
        if (customerService.existsByEmail(customerDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        // Create customer object from DTO
        Customer customer = new Customer();
        customer.setFName(customerDTO.getFName());
        customer.setMName(customerDTO.getMName());
        customer.setLName(customerDTO.getLName());
        customer.setEmail(customerDTO.getEmail());
        customer.setBirthDate(customerDTO.getBirthDate());
        customer.setGender(customerDTO.getGender());
        customer.setActive(true);
        
        // Save customer
        Customer savedCustomer = customerService.save(customer);
        return new ResponseEntity<>(CustomerDTO.fromEntity(savedCustomer), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or @customerAuthorizationService.isCustomerAuthorized(authentication, #id)")
    @Operation(summary = "Update customer", description = "Updates an existing customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody CustomerDTO customerDTO) {
        logger.info("Updating customer with ID: {}", id);
        
        // Check if customer exists
        Customer existingCustomer = customerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        // Check if email exists for another customer
        if (!existingCustomer.getEmail().equals(customerDTO.getEmail()) && 
                customerService.existsByEmail(customerDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        // Update customer using DTO helper method
        existingCustomer = customerDTO.updateEntity(existingCustomer);
        
        Customer updatedCustomer = customerService.update(existingCustomer);
        return ResponseEntity.ok(CustomerDTO.fromEntity(updatedCustomer));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @customerAuthorizationService.isCustomerAuthorized(authentication, #id)")
    @Operation(summary = "Delete customer", description = "Deletes a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable Integer id) {
        logger.info("Deleting customer with ID: {}", id);
        
        if (!customerService.existsById(id)) {
            throw new ResourceNotFoundException("Customer", "id", id);
        }
        
        customerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Search customers", description = "Search customers by name or email")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        logger.info("Searching customers with name: {} and email: {}", name, email);
        
        List<Customer> customers;
        
        if (name != null && !name.isEmpty()) {
            // Try first name first, then last name
            customers = customerService.findByFirstName(name);
            if (customers.isEmpty()) {
                customers = customerService.findByLastName(name);
            }
        } else if (email != null && !email.isEmpty()) {
            customers = customerService.findByEmail(email)
                .map(List::of).orElse(List.of());
        } else {
            customers = customerService.findAll();
        }
        
        List<CustomerDTO> customerDTOs = customers.stream()
                .map(CustomerDTO::fromEntity)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(customerDTOs);
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Count customers", description = "Returns the total number of customers")
    public ResponseEntity<Long> countCustomers() {
        logger.info("Counting customers");
        
        long count = customerService.count();
        return ResponseEntity.ok(count);
    }
    
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Activate customer", description = "Activates a customer account")
    public ResponseEntity<CustomerDTO> activateCustomer(@PathVariable Integer id) {
        logger.info("Activating customer account with ID: {}", id);
        
        Customer customer = customerService.restoreCustomer(id);
        return ResponseEntity.ok(CustomerDTO.fromEntity(customer));
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Deactivate customer", description = "Deactivates a customer account")
    public ResponseEntity<CustomerDTO> deactivateCustomer(@PathVariable Integer id) {
        logger.info("Deactivating customer account with ID: {}", id);
        
        Customer customer = customerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        customer.setActive(false);
        Customer updatedCustomer = customerService.update(customer);
        return ResponseEntity.ok(CustomerDTO.fromEntity(updatedCustomer));
    }
} 