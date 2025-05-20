package com.example.demo.repositories;

import com.example.demo.entities.Customer;
import com.example.demo.entities.CustomerOrder;
import com.example.demo.entities.Employee;
import com.example.demo.entities.PaymentMethod;
import com.example.demo.entities.Promotion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {
    List<CustomerOrder> findByCustomerId(Integer customerId);
    List<CustomerOrder> findByCustomerIdAndStatus(Integer customerId, String status);
    List<CustomerOrder> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);
    List<CustomerOrder> findByStatus(String status);
    List<CustomerOrder> findByEmployeeSellerId(Integer employeeId);
    List<CustomerOrder> findByAddressId(Integer addressId);
    List<CustomerOrder> findOrdersWithReturns();
    List<CustomerOrder> findByCustomer(Customer customer);
    List<CustomerOrder> findByEmployeeSeller(Employee employee);
    List<CustomerOrder> findByPaymentMethod(PaymentMethod paymentMethod);
    List<CustomerOrder> findByPromotion(Promotion promotion);
    List<CustomerOrder> findByPurchaseDate(LocalDate date);
    List<CustomerOrder> findByTotalPriceGreaterThanEqual(BigDecimal minPrice);
    List<CustomerOrder> findByTotalPriceLessThanEqual(BigDecimal maxPrice);
    List<CustomerOrder> findByCustomerAndStatus(Customer customer, String status);
    List<CustomerOrder> findRecentOrders(LocalDate date);
    BigDecimal calculateTotalRevenue(LocalDate startDate, LocalDate endDate);
    BigDecimal calculateTotalRevenueByCustomer(Customer customer);
    BigDecimal calculateTotalRevenueByEmployee(Employee employee);
    long countByStatus(String status);
    List<Object[]> findTopCustomersByOrderCount();
    List<Object[]> findTopEmployeesBySales();
}
