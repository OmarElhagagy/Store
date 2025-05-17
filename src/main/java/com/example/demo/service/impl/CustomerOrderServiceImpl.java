package com.example.demo.service.impl;

import com.example.demo.entities.*;
import com.example.demo.repositories.CustomerOrderRepository;
import com.example.demo.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;

    @Autowired
    public CustomerOrderServiceImpl(CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    @Override
    @Transactional
    public CustomerOrder createOrder(Customer customer, Employee employeeSeller, 
                                   PaymentMethod paymentMethod, Address address,
                                   Promotion promotion, BigDecimal totalPrice, String status) {
        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setEmployeeSeller(employeeSeller);
        order.setPaymentMethod(paymentMethod);
        order.setAddress(address);
        order.setPromotion(promotion);
        order.setTotalPrice(totalPrice);
        order.setStatus(status);
        order.setPurchaseDate(LocalDate.now());
        order.setOrderDetails(new HashSet<>());
        order.setReturnFields(new HashSet<>());
        order.setShippings(new HashSet<>());
        
        return customerOrderRepository.save(order);
    }

    @Override
    public Optional<CustomerOrder> findOrderById(Integer id) {
        return customerOrderRepository.findById(id);
    }

    @Override
    public List<CustomerOrder> getAllOrders() {
        return customerOrderRepository.findAll();
    }

    @Override
    public List<CustomerOrder> getOrdersByCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        return customerOrderRepository.findByCustomer(customer);
    }

    @Override
    public List<CustomerOrder> getOrdersByEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        return customerOrderRepository.findByEmployeeSeller(employee);
    }

    @Override
    public List<CustomerOrder> getOrdersByPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        return customerOrderRepository.findByPaymentMethod(paymentMethod);
    }

    @Override
    public List<CustomerOrder> getOrdersByPromotion(Promotion promotion) {
        if (promotion == null) {
            throw new IllegalArgumentException("Promotion cannot be null");
        }
        return customerOrderRepository.findByPromotion(promotion);
    }

    @Override
    public List<CustomerOrder> getOrdersByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        return customerOrderRepository.findByStatus(status);
    }

    @Override
    public List<CustomerOrder> getOrdersByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return customerOrderRepository.findByPurchaseDate(date);
    }

    @Override
    public List<CustomerOrder> getOrdersBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        return customerOrderRepository.findByPurchaseDateBetween(startDate, endDate);
    }

    @Override
    public List<CustomerOrder> getOrdersWithMinimumTotalPrice(BigDecimal minPrice) {
        if (minPrice == null) {
            throw new IllegalArgumentException("Minimum price cannot be null");
        }
        return customerOrderRepository.findByTotalPriceGreaterThanEqual(minPrice);
    }

    @Override
    public List<CustomerOrder> getOrdersWithMaximumTotalPrice(BigDecimal maxPrice) {
        if (maxPrice == null) {
            throw new IllegalArgumentException("Maximum price cannot be null");
        }
        return customerOrderRepository.findByTotalPriceLessThanEqual(maxPrice);
    }

    @Override
    public List<CustomerOrder> getOrdersByCustomerAndStatus(Customer customer, String status) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        return customerOrderRepository.findByCustomerAndStatus(customer, status);
    }

    @Override
    public List<CustomerOrder> getRecentOrders(int daysBack) {
        if (daysBack < 0) {
            throw new IllegalArgumentException("Days back cannot be negative");
        }
        LocalDate date = LocalDate.now().minusDays(daysBack);
        return customerOrderRepository.findRecentOrders(date);
    }

    @Override
    public List<CustomerOrder> getOrdersWithReturns() {
        return customerOrderRepository.findOrdersWithReturns();
    }

    @Override
    public BigDecimal calculateTotalRevenue(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        BigDecimal revenue = customerOrderRepository.calculateTotalRevenue(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTotalRevenueByCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        
        BigDecimal revenue = customerOrderRepository.calculateTotalRevenueByCustomer(customer);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTotalRevenueByEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        
        BigDecimal revenue = customerOrderRepository.calculateTotalRevenueByEmployee(employee);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public long getOrderCountByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        return customerOrderRepository.countByStatus(status);
    }

    @Override
    public Map<Customer, Long> getTopCustomersByOrderCount(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero");
        }
        
        List<Object[]> results = customerOrderRepository.findTopCustomersByOrderCount();
        
        Map<Customer, Long> topCustomers = new LinkedHashMap<>();
        int count = 0;
        
        for (Object[] result : results) {
            if (count >= limit) break;
            
            Customer customer = (Customer) result[0];
            Long orderCount = (Long) result[1];
            topCustomers.put(customer, orderCount);
            count++;
        }
        
        return topCustomers;
    }

    @Override
    public Map<Employee, BigDecimal> getTopEmployeesBySales(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero");
        }
        
        List<Object[]> results = customerOrderRepository.findTopEmployeesBySales();
        
        Map<Employee, BigDecimal> topEmployees = new LinkedHashMap<>();
        int count = 0;
        
        for (Object[] result : results) {
            if (count >= limit) break;
            
            Employee employee = (Employee) result[0];
            BigDecimal sales = (BigDecimal) result[1];
            topEmployees.put(employee, sales);
            count++;
        }
        
        return topEmployees;
    }

    @Override
    @Transactional
    public CustomerOrder updateOrder(CustomerOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        if (!customerOrderRepository.existsById(order.getId())) {
            throw new IllegalArgumentException("Order not found with ID: " + order.getId());
        }
        
        return customerOrderRepository.save(order);
    }

    @Override
    @Transactional
    public CustomerOrder updateOrderStatus(Integer orderId, String status) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        
        Optional<CustomerOrder> optionalOrder = customerOrderRepository.findById(orderId);
        
        if (optionalOrder.isPresent()) {
            CustomerOrder order = optionalOrder.get();
            order.setStatus(status);
            return customerOrderRepository.save(order);
        }
        
        throw new IllegalArgumentException("Order not found with ID: " + orderId);
    }

    @Override
    public Set<OrderDetail> getOrderDetails(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        
        Optional<CustomerOrder> optionalOrder = customerOrderRepository.findById(orderId);
        
        if (optionalOrder.isPresent()) {
            return optionalOrder.get().getOrderDetails();
        }
        
        throw new IllegalArgumentException("Order not found with ID: " + orderId);
    }

    @Override
    public Set<Return> getOrderReturns(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        
        Optional<CustomerOrder> optionalOrder = customerOrderRepository.findById(orderId);
        
        if (optionalOrder.isPresent()) {
            return optionalOrder.get().getReturnFields();
        }
        
        throw new IllegalArgumentException("Order not found with ID: " + orderId);
    }

    @Override
    public Set<Shipping> getOrderShippings(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        
        Optional<CustomerOrder> optionalOrder = customerOrderRepository.findById(orderId);
        
        if (optionalOrder.isPresent()) {
            return optionalOrder.get().getShippings();
        }
        
        throw new IllegalArgumentException("Order not found with ID: " + orderId);
    }

    @Override
    public BigDecimal calculateOrderTotal(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        
        Optional<CustomerOrder> optionalOrder = customerOrderRepository.findById(orderId);
        
        if (optionalOrder.isPresent()) {
            CustomerOrder order = optionalOrder.get();
            
            // If there are no order details, return the stored total price
            if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
                return order.getTotalPrice();
            }
            
            // Calculate total from order details
            BigDecimal total = BigDecimal.ZERO;
            for (OrderDetail detail : order.getOrderDetails()) {
                BigDecimal lineTotal = detail.getPrice().multiply(new BigDecimal(detail.getQuantity()));
                total = total.add(lineTotal);
            }
            
            // Apply promotion discount if applicable
            if (order.getPromotion() != null && order.getPromotion().getDiscountRate() != null) {
                BigDecimal discount = total.multiply(order.getPromotion().getDiscountRate());
                total = total.subtract(discount);
            }
            
            return total;
        }
        
        throw new IllegalArgumentException("Order not found with ID: " + orderId);
    }

    @Override
    @Transactional
    public void deleteOrder(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        
        if (!customerOrderRepository.existsById(orderId)) {
            throw new IllegalArgumentException("Order not found with ID: " + orderId);
        }
        
        customerOrderRepository.deleteById(orderId);
    }
}