package com.example.demo.controller;

import com.example.demo.dto.CustomerOrderDTO;
import com.example.demo.entities.Customer;
import com.example.demo.entities.CustomerOrder;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.service.CustomerOrderService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Controller", description = "API to manage customer orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final CustomerOrderService orderService;
    private final CustomerService customerService;

    @Autowired
    public OrderController(CustomerOrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get all orders", description = "Returns a list of all orders")
    public ResponseEntity<List<CustomerOrderDTO>> getAllOrders() {
        logger.info("Fetching all orders");
        List<CustomerOrder> orders = orderService.getAllOrders();
        List<CustomerOrderDTO> orderDTOs = orders.stream()
                .map(CustomerOrderDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or @customerAuthorizationService.isOrderAuthorized(authentication, #id)")
    @Operation(summary = "Get order by ID", description = "Returns a single order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<CustomerOrderDTO> getOrderById(
            @Parameter(description = "Order ID", required = true) @PathVariable Integer id) {
        logger.info("Fetching order with ID: {}", id);
        
        CustomerOrder order = orderService.findOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        return ResponseEntity.ok(CustomerOrderDTO.fromEntity(order));
    }

    @PostMapping
    @Operation(summary = "Create new order", description = "Creates a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerOrderDTO> createOrder(@Valid @RequestBody CustomerOrderDTO orderDTO) {
        logger.info("Creating new order for customer ID: {}", orderDTO.getCustomerId());
        
        Customer customer = customerService.findCustomerById(orderDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", orderDTO.getCustomerId()));
        
        CustomerOrder order = orderService.createOrder(customer, orderDTO.getOrderDate(), orderDTO.getStatus());
        return new ResponseEntity<>(CustomerOrderDTO.fromEntity(order), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update order", description = "Updates an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<CustomerOrderDTO> updateOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody CustomerOrderDTO orderDTO) {
        logger.info("Updating order with ID: {}", id);
        
        CustomerOrder existingOrder = orderService.findOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        if (orderDTO.getCustomerId() != null && !orderDTO.getCustomerId().equals(existingOrder.getCustomer().getId())) {
            Customer newCustomer = customerService.findCustomerById(orderDTO.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", orderDTO.getCustomerId()));
            existingOrder.setCustomer(newCustomer);
        }
        
        existingOrder.setOrderDate(orderDTO.getOrderDate());
        existingOrder.setStatus(orderDTO.getStatus());
        
        CustomerOrder updatedOrder = orderService.updateOrder(existingOrder);
        return ResponseEntity.ok(CustomerOrderDTO.fromEntity(updatedOrder));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete order", description = "Deletes an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID", required = true) @PathVariable Integer id) {
        logger.info("Deleting order with ID: {}", id);
        
        if (!orderService.findOrderById(id).isPresent()) {
            throw new ResourceNotFoundException("Order", "id", id);
        }
        
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or @customerAuthorizationService.isCustomerAuthorized(authentication, #customerId)")
    @Operation(summary = "Get orders by customer", description = "Returns orders for a specific customer")
    public ResponseEntity<List<CustomerOrderDTO>> getOrdersByCustomer(@PathVariable Integer customerId) {
        logger.info("Fetching orders for customer ID: {}", customerId);
        
        Customer customer = customerService.findCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
        
        List<CustomerOrder> orders = orderService.getOrdersByCustomer(customer);
        List<CustomerOrderDTO> orderDTOs = orders.stream()
                .map(CustomerOrderDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get orders by status", description = "Returns orders with a specific status")
    public ResponseEntity<List<CustomerOrderDTO>> getOrdersByStatus(@PathVariable String status) {
        logger.info("Fetching orders with status: {}", status);
        
        List<CustomerOrder> orders = orderService.getOrdersByStatus(status);
        List<CustomerOrderDTO> orderDTOs = orders.stream()
                .map(CustomerOrderDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Get orders by date range", description = "Returns orders within a date range")
    public ResponseEntity<List<CustomerOrderDTO>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("Fetching orders between {} and {}", startDate, endDate);
        
        List<CustomerOrder> orders = orderService.getOrdersByDateRange(startDate, endDate);
        List<CustomerOrderDTO> orderDTOs = orders.stream()
                .map(CustomerOrderDTO::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(orderDTOs);
    }
    
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Update order status", description = "Updates the status of an order")
    public ResponseEntity<CustomerOrderDTO> updateOrderStatus(
            @PathVariable Integer id, 
            @RequestParam String status) {
        logger.info("Updating status for order ID: {} to {}", id, status);
        
        CustomerOrder order = orderService.findOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        
        CustomerOrder updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(CustomerOrderDTO.fromEntity(updatedOrder));
    }
} 