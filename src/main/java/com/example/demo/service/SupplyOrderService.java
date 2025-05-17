package com.example.demo.service;

import com.example.demo.entities.Employee;
import com.example.demo.entities.Product;
import com.example.demo.entities.Supplier;
import com.example.demo.entities.SupplyOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SupplyOrderService {
    
    // Create a new supply order
    SupplyOrder createSupplyOrder(Supplier supplier, Product product, Employee employee, 
                                 Integer quantityOrdered, BigDecimal price);
    
    // Find supply order by ID
    Optional<SupplyOrder> findSupplyOrderById(Integer id);
    
    // Get all supply orders
    List<SupplyOrder> getAllSupplyOrders();
    
    // Get supply orders by supplier
    List<SupplyOrder> getSupplyOrdersBySupplier(Supplier supplier);
    
    // Get supply orders by product
    List<SupplyOrder> getSupplyOrdersByProduct(Product product);
    
    // Get supply orders by employee
    List<SupplyOrder> getSupplyOrdersByEmployee(Employee employee);
    
    // Get supply orders by date
    List<SupplyOrder> getSupplyOrdersByDate(LocalDate date);
    
    // Get supply orders between dates
    List<SupplyOrder> getSupplyOrdersBetweenDates(LocalDate startDate, LocalDate endDate);
    
    // Get supply orders with minimum quantity
    List<SupplyOrder> getSupplyOrdersWithMinimumQuantity(Integer minQuantity);
    
    // Get supply orders with minimum price
    List<SupplyOrder> getSupplyOrdersWithMinimumPrice(BigDecimal minPrice);
    
    // Get supply orders with maximum price
    List<SupplyOrder> getSupplyOrdersWithMaximumPrice(BigDecimal maxPrice);
    
    // Get recent supply orders
    List<SupplyOrder> getRecentSupplyOrders(int daysBack);
    
    // Get supply orders by supplier and product
    List<SupplyOrder> getSupplyOrdersBySupplierAndProduct(Supplier supplier, Product product);
    
    // Get supply orders by supplier and date range
    List<SupplyOrder> getSupplyOrdersBySupplierAndDateRange(Supplier supplier, 
                                                          LocalDate startDate, 
                                                          LocalDate endDate);
    
    // Calculate total value of orders by supplier
    BigDecimal calculateTotalValueBySupplier(Supplier supplier);
    
    // Calculate total value of orders by product
    BigDecimal calculateTotalValueByProduct(Product product);
    
    // Update supply order
    SupplyOrder updateSupplyOrder(SupplyOrder supplyOrder);
    
    // Update order quantity and price
    SupplyOrder updateOrderDetails(Integer supplyOrderId, Integer quantityOrdered, BigDecimal price);
    
    // Assign employee to order
    SupplyOrder assignEmployeeToOrder(Integer supplyOrderId, Employee employee);
    
    // Delete supply order
    void deleteSupplyOrder(Integer supplyOrderId);
}