package com.example.demo.service;

import com.example.demo.entities.CustomerOrder;
import com.example.demo.entities.PaymentMethod;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PaymentMethodService {
    
    // Create a new payment method
    PaymentMethod createPaymentMethod(String methodName);
    
    // Find payment method by ID
    Optional<PaymentMethod> findPaymentMethodById(Integer id);
    
    // Find payment method by name
    Optional<PaymentMethod> findPaymentMethodByName(String methodName);
    
    // Get all payment methods
    List<PaymentMethod> getAllPaymentMethods();
    
    // Get payment methods by name search
    List<PaymentMethod> getPaymentMethodsByNameSearch(String keyword);
    
    // Get payment methods with orders
    List<PaymentMethod> getPaymentMethodsWithOrders();
    
    // Get payment methods without orders
    List<PaymentMethod> getPaymentMethodsWithoutOrders();
    
    // Get most used payment methods
    Map<PaymentMethod, Long> getMostUsedPaymentMethods(int limit);
    
    // Update payment method
    PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod);
    
    // Update payment method name
    PaymentMethod updatePaymentMethodName(Integer paymentMethodId, String methodName);
    
    // Get orders by payment method
    Set<CustomerOrder> getOrdersByPaymentMethod(Integer paymentMethodId);
    
    // Count orders by payment method
    long countOrdersByPaymentMethod(Integer paymentMethodId);
    
    // Check if payment method exists by name
    boolean isPaymentMethodNameTaken(String methodName);
    
    // Delete payment method
    void deletePaymentMethod(Integer paymentMethodId);
}