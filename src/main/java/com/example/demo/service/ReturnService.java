package com.example.demo.service;

import com.example.demo.entities.Return;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReturnService {

    // Create a new return
    Return createReturn(Return returnRequest);
    
    // Get return by ID
    Return getReturnById(Integer id);
    
    // Get all returns for an order
    List<Return> getReturnsByOrder(Integer orderId);
    
    // Get all returns for a product
    List<Return> getReturnsByProduct(Integer productId);
    
    // Get returns within date range
    List<Return> getReturnsByDateRange(LocalDate startDate, LocalDate endDate);
    
    // Search returns by reason
    List<Return> searchReturnsByReason(String reason);
    
    // Get total refunds for a product
    BigDecimal getTotalRefundsForProduct(Integer productId);
    
    // Get return count for product
    long getReturnCountForProduct(Integer productId);
    
    // Get high value returns
    List<Return> getHighValueReturns(BigDecimal minAmount);
    
    // Delete a return
    void deleteReturn(Integer id);
}