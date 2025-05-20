package com.example.demo.controller;

import com.example.demo.dto.CustomerOrderDTO;
import com.example.demo.entities.CustomerOrder;
import com.example.demo.service.CustomerOrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for managing customer orders
 */
@RestController
@RequestMapping("/api/orders")
@Slf4j
public class CustomerOrderController {

    private final CustomerOrderService orderService;

    @Autowired
    public CustomerOrderController(CustomerOrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get all orders with pagination
     * Restricted to admin users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CustomerOrderDTO>> getAllOrders(Pageable pageable) {
        log.info("REST request to get all Orders with pagination");
        Page<CustomerOrder> orders = orderService.findAllWithPagination(pageable);
        Page<CustomerOrderDTO> orderDTOs = orders.map(CustomerOrderDTO::fromEntity);
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Get orders by customer
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#customerId)")
    public ResponseEntity<List<CustomerOrderDTO>> getOrdersByCustomer(@PathVariable Integer customerId) {
        log.info("REST request to get Orders for customer ID {}", customerId);
        List<CustomerOrder> orders = orderService.findByCustomerId(customerId);
        List<CustomerOrderDTO> orderDTOs = orders.stream()
                .map(CustomerOrderDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Get orders by date range
     * Restricted to admin users
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerOrderDTO>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("REST request to get Orders between {} and {}", startDate, endDate);
        List<CustomerOrder> orders = orderService.findByOrderDateBetween(startDate, endDate);
        List<CustomerOrderDTO> orderDTOs = orders.stream()
                .map(CustomerOrderDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Get orders by status
     * Restricted to admin and staff users
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<CustomerOrderDTO>> getOrdersByStatus(@PathVariable String status) {
        log.info("REST request to get Orders with status {}", status);
        List<CustomerOrder> orders = orderService.findByStatus(status);
        List<CustomerOrderDTO> orderDTOs = orders.stream()
                .map(CustomerOrderDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    /**
     * Get a specific order by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderResourceOwner(#id)")
    public ResponseEntity<CustomerOrderDTO> getOrder(@PathVariable Integer id) {
        log.info("REST request to get Order with ID {}", id);
        Optional<CustomerOrder> order = orderService.findById(id);
        return order
                .map(value -> ResponseEntity.ok(CustomerOrderDTO.fromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new order
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerResourceOwner(#orderDTO.customerId)")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CustomerOrderDTO orderDTO, BindingResult bindingResult) {
        log.info("REST request to create Order for customer ID {}", orderDTO.getCustomerId());
        
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        
        try {
            CustomerOrder order = orderService.createOrderFromDTO(orderDTO);
            return new ResponseEntity<>(CustomerOrderDTO.fromEntity(order), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error creating order", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating order: " + e.getMessage(), e);
        }
    }

    /**
     * Update order status
     * Restricted to admin and staff users
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<CustomerOrderDTO> updateOrderStatus(
            @PathVariable Integer id, 
            @RequestParam String status) {
        log.info("REST request to update Order ID {} status to {}", id, status);
        try {
            CustomerOrder order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(CustomerOrderDTO.fromEntity(order));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error updating order status", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating order status: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel an order
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderResourceOwner(#id)")
    public ResponseEntity<CustomerOrderDTO> cancelOrder(@PathVariable Integer id) {
        log.info("REST request to cancel Order with ID {}", id);
        try {
            CustomerOrder order = orderService.cancelOrder(id);
            return ResponseEntity.ok(CustomerOrderDTO.fromEntity(order));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error cancelling order", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error cancelling order: " + e.getMessage(), e);
        }
    }

    /**
     * Process payment for an order
     */
    @PostMapping("/{id}/payment")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderResourceOwner(#id)")
    public ResponseEntity<CustomerOrderDTO> processPayment(
            @PathVariable Integer id, 
            @RequestParam Integer paymentMethodId) {
        log.info("REST request to process payment for Order ID {} with payment method {}", id, paymentMethodId);
        try {
            CustomerOrder order = orderService.processPayment(id, paymentMethodId);
            return ResponseEntity.ok(CustomerOrderDTO.fromEntity(order));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error processing payment", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing payment: " + e.getMessage(), e);
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