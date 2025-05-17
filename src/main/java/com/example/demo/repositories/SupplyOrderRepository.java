package com.example.demo.repositories;

import com.example.demo.entities.Employee;
import com.example.demo.entities.Product;
import com.example.demo.entities.Supplier;
import com.example.demo.entities.SupplyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Integer> {
    
    // Find by supplier
    List<SupplyOrder> findBySupplier(Supplier supplier);
    
    // Find by product
    List<SupplyOrder> findByProduct(Product product);
    
    // Find by employee
    List<SupplyOrder> findByEmployee(Employee employee);
    
    // Find by order date
    List<SupplyOrder> findByOrderDate(LocalDate orderDate);
    
    // Find orders between dates
    List<SupplyOrder> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find orders with quantity greater than
    List<SupplyOrder> findByQuantityOrderedGreaterThanEqual(Integer minQuantity);
    
    // Find orders with price greater than
    List<SupplyOrder> findByPriceGreaterThanEqual(BigDecimal minPrice);
    
    // Find orders with price less than
    List<SupplyOrder> findByPriceLessThanEqual(BigDecimal maxPrice);
    
    // Find recent orders (last 30 days)
    @Query("SELECT so FROM SupplyOrder so WHERE so.orderDate >= :date")
    List<SupplyOrder> findRecentOrders(LocalDate date);
    
    // Find orders by supplier and product
    List<SupplyOrder> findBySupplierAndProduct(Supplier supplier, Product product);
    
    // Find orders by supplier and date range
    List<SupplyOrder> findBySupplierAndOrderDateBetween(Supplier supplier, LocalDate startDate, LocalDate endDate);
    
    // Calculate total value of orders by supplier
    @Query("SELECT SUM(so.price * so.quantityOrdered) FROM SupplyOrder so WHERE so.supplier = :supplier")
    BigDecimal calculateTotalValueBySupplier(Supplier supplier);
    
    // Calculate total value of orders by product
    @Query("SELECT SUM(so.price * so.quantityOrdered) FROM SupplyOrder so WHERE so.product = :product")
    BigDecimal calculateTotalValueByProduct(Product product);
}