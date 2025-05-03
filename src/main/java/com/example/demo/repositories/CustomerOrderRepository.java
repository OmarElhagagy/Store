package com.example.demo.repositories;

import com.example.demo.entities.CustomerOrder;

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
}
