package com.example.demo.service;

import com.example.demo.entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CustomerOrderService {
    
    // Create a new order
    CustomerOrder createOrder(Customer customer, Employee employeeSeller, 
                             PaymentMethod paymentMethod, Address address,
                             Promotion promotion, BigDecimal totalPrice, String status);
    
    // Find order by ID
    Optional<CustomerOrder> findOrderById(Integer id);
    
    // Get all orders
    List<CustomerOrder> getAllOrders();
    
    // Get orders by customer
    List<CustomerOrder> getOrdersByCustomer(Customer customer);
    
    // Get orders by employee seller
    List<CustomerOrder> getOrdersByEmployee(Employee employee);
    
    // Get orders by payment method
    List<CustomerOrder> getOrdersByPaymentMethod(PaymentMethod paymentMethod);
    
    // Get orders by promotion
    List<CustomerOrder> getOrdersByPromotion(Promotion promotion);
    
    // Get orders by status
    List<CustomerOrder> getOrdersByStatus(String status);
    
    // Get orders by date
    List<CustomerOrder> getOrdersByDate(LocalDate date);
    
    // Get orders between dates
    List<CustomerOrder> getOrdersBetweenDates(LocalDate startDate, LocalDate endDate);
    
    // Get orders with minimum total price
    List<CustomerOrder> getOrdersWithMinimumTotalPrice(BigDecimal minPrice);
    
    // Get orders with maximum total price
    List<CustomerOrder> getOrdersWithMaximumTotalPrice(BigDecimal maxPrice);
    
    // Get orders by customer and status
    List<CustomerOrder> getOrdersByCustomerAndStatus(Customer customer, String status);
    
    // Get recent orders
    List<CustomerOrder> getRecentOrders(int daysBack);
    
    // Get orders with returns
    List<CustomerOrder> getOrdersWithReturns();
    
    // Calculate total revenue by date range
    BigDecimal calculateTotalRevenue(LocalDate startDate, LocalDate endDate);
    
    // Calculate total revenue by customer
    BigDecimal calculateTotalRevenueByCustomer(Customer customer);
    
    // Calculate total revenue by employee
    BigDecimal calculateTotalRevenueByEmployee(Employee employee);
    
    // Get order count by status
    long getOrderCountByStatus(String status);
    
    // Get top customers by order count
    Map<Customer, Long> getTopCustomersByOrderCount(int limit);
    
    // Get top employees by sales
    Map<Employee, BigDecimal> getTopEmployeesBySales(int limit);
    
    // Update order
    CustomerOrder updateOrder(CustomerOrder order);
    
    // Update order status
    CustomerOrder updateOrderStatus(Integer orderId, String status);
    
    // Get order details
    Set<OrderDetail> getOrderDetails(Integer orderId);
    
    // Get order returns
    Set<Return> getOrderReturns(Integer orderId);
    
    // Get order shipping records
    Set<Shipping> getOrderShippings(Integer orderId);
    
    // Calculate order total
    BigDecimal calculateOrderTotal(Integer orderId);
    
    // Delete order
    void deleteOrder(Integer orderId);
}