package com.example.demo.repositories;

import com.example.demo.entities.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    
    // Find by method name
    Optional<PaymentMethod> findByMethodName(String methodName);
    
    // Find by method name containing
    List<PaymentMethod> findByMethodNameContaining(String keyword);
    
    // Find payment methods with orders
    @Query("SELECT DISTINCT pm FROM PaymentMethod pm JOIN pm.customerOrders")
    List<PaymentMethod> findPaymentMethodsWithOrders();
    
    // Find payment methods without orders
    @Query("SELECT pm FROM PaymentMethod pm WHERE SIZE(pm.customerOrders) = 0")
    List<PaymentMethod> findPaymentMethodsWithoutOrders();
    
    // Find most used payment methods
    @Query("SELECT pm, COUNT(o) as orderCount FROM PaymentMethod pm JOIN pm.customerOrders o GROUP BY pm ORDER BY orderCount DESC")
    List<Object[]> findMostUsedPaymentMethods();
    
    // Check if payment method exists by name
    boolean existsByMethodName(String methodName);
}