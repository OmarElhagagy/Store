package com.example.demo.service.impl;

import com.example.demo.entities.CustomerOrder;
import com.example.demo.entities.PaymentMethod;
import com.example.demo.repositories.PaymentMethodRepository;
import com.example.demo.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    @Transactional
    public PaymentMethod createPaymentMethod(String methodName) {
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new IllegalArgumentException("Method name cannot be null or empty");
        }
        
        if (paymentMethodRepository.existsByMethodName(methodName)) {
            throw new IllegalArgumentException("Payment method with this name already exists: " + methodName);
        }
        
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setMethodName(methodName);
        paymentMethod.setCustomerOrders(new HashSet<>());
        
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public Optional<PaymentMethod> findPaymentMethodById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Payment method ID cannot be null");
        }
        return paymentMethodRepository.findById(id);
    }

    @Override
    public Optional<PaymentMethod> findPaymentMethodByName(String methodName) {
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new IllegalArgumentException("Method name cannot be null or empty");
        }
        return paymentMethodRepository.findByMethodName(methodName);
    }

    @Override
    public List<PaymentMethod> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    @Override
    public List<PaymentMethod> getPaymentMethodsByNameSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be null or empty");
        }
        return paymentMethodRepository.findByMethodNameContaining(keyword);
    }

    @Override
    public List<PaymentMethod> getPaymentMethodsWithOrders() {
        return paymentMethodRepository.findPaymentMethodsWithOrders();
    }

    @Override
    public List<PaymentMethod> getPaymentMethodsWithoutOrders() {
        return paymentMethodRepository.findPaymentMethodsWithoutOrders();
    }

    @Override
    public Map<PaymentMethod, Long> getMostUsedPaymentMethods(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than zero");
        }
        
        List<Object[]> results = paymentMethodRepository.findMostUsedPaymentMethods();
        
        Map<PaymentMethod, Long> mostUsedMethods = new LinkedHashMap<>();
        int count = 0;
        
        for (Object[] result : results) {
            if (count >= limit) break;
            
            PaymentMethod paymentMethod = (PaymentMethod) result[0];
            Long orderCount = (Long) result[1];
            mostUsedMethods.put(paymentMethod, orderCount);
            count++;
        }
        
        return mostUsedMethods;
    }

    @Override
    @Transactional
    public PaymentMethod updatePaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        
        if (!paymentMethodRepository.existsById(paymentMethod.getId())) {
            throw new IllegalArgumentException("Payment method not found with ID: " + paymentMethod.getId());
        }
        
        // Verify name uniqueness if it was changed
        Optional<PaymentMethod> existingMethod = paymentMethodRepository.findById(paymentMethod.getId());
        if (existingMethod.isPresent() && !existingMethod.get().getMethodName().equals(paymentMethod.getMethodName())) {
            if (paymentMethodRepository.existsByMethodName(paymentMethod.getMethodName())) {
                throw new IllegalArgumentException("Payment method with this name already exists: " + paymentMethod.getMethodName());
            }
        }
        
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    @Transactional
    public PaymentMethod updatePaymentMethodName(Integer paymentMethodId, String methodName) {
        if (paymentMethodId == null) {
            throw new IllegalArgumentException("Payment method ID cannot be null");
        }
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new IllegalArgumentException("Method name cannot be null or empty");
        }
        
        Optional<PaymentMethod> optionalPaymentMethod = paymentMethodRepository.findById(paymentMethodId);
        
        if (optionalPaymentMethod.isPresent()) {
            PaymentMethod paymentMethod = optionalPaymentMethod.get();
            
            // Check if the name already exists for another payment method
            if (!methodName.equals(paymentMethod.getMethodName()) && paymentMethodRepository.existsByMethodName(methodName)) {
                throw new IllegalArgumentException("Payment method with this name already exists: " + methodName);
            }
            
            paymentMethod.setMethodName(methodName);
            return paymentMethodRepository.save(paymentMethod);
        }
        
        throw new IllegalArgumentException("Payment method not found with ID: " + paymentMethodId);
    }

    @Override
    public Set<CustomerOrder> getOrdersByPaymentMethod(Integer paymentMethodId) {
        if (paymentMethodId == null) {
            throw new IllegalArgumentException("Payment method ID cannot be null");
        }
        
        Optional<PaymentMethod> optionalPaymentMethod = paymentMethodRepository.findById(paymentMethodId);
        
        if (optionalPaymentMethod.isPresent()) {
            return optionalPaymentMethod.get().getCustomerOrders();
        }
        
        throw new IllegalArgumentException("Payment method not found with ID: " + paymentMethodId);
    }

    @Override
    public long countOrdersByPaymentMethod(Integer paymentMethodId) {
        if (paymentMethodId == null) {
            throw new IllegalArgumentException("Payment method ID cannot be null");
        }
        
        Optional<PaymentMethod> optionalPaymentMethod = paymentMethodRepository.findById(paymentMethodId);
        
        if (optionalPaymentMethod.isPresent()) {
            return optionalPaymentMethod.get().getCustomerOrders().size();
        }
        
        throw new IllegalArgumentException("Payment method not found with ID: " + paymentMethodId);
    }

    @Override
    public boolean isPaymentMethodNameTaken(String methodName) {
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new IllegalArgumentException("Method name cannot be null or empty");
        }
        return paymentMethodRepository.existsByMethodName(methodName);
    }

    @Override
    @Transactional
    public void deletePaymentMethod(Integer paymentMethodId) {
        if (paymentMethodId == null) {
            throw new IllegalArgumentException("Payment method ID cannot be null");
        }
        
        Optional<PaymentMethod> optionalPaymentMethod = paymentMethodRepository.findById(paymentMethodId);
        
        if (optionalPaymentMethod.isPresent()) {
            PaymentMethod paymentMethod = optionalPaymentMethod.get();
            
            // Check if payment method is used in any orders
            if (!paymentMethod.getCustomerOrders().isEmpty()) {
                throw new IllegalStateException("Cannot delete payment method that is used in orders");
            }
            
            paymentMethodRepository.deleteById(paymentMethodId);
        } else {
            throw new IllegalArgumentException("Payment method not found with ID: " + paymentMethodId);
        }
    }
}