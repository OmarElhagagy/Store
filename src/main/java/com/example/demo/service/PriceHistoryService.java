package com.example.demo.service;

import com.example.demo.entities.PriceHistory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PriceHistoryService {

    // Record a new price change
    PriceHistory recordPriceChange(Integer productId, BigDecimal newPrice);
    
    // Get all price changes for a product
    List<PriceHistory> getPriceHistoryForProduct(Integer productId);
    
    // Get price history within date range
    List<PriceHistory> getPriceHistoryForProductBetweenDates(Integer productId, LocalDate startDate, LocalDate endDate);
    
    // Get current price
    Optional<BigDecimal> getCurrentPrice(Integer productId);
    
    // Get price at specific date
    Optional<BigDecimal> getPriceAtDate(Integer productId, LocalDate date);
    
    // Get significant price changes
    List<PriceHistory> getSignificantPriceChanges(Integer productId, BigDecimal threshold);
    
    // Get average price
    Optional<BigDecimal> getAveragePrice(Integer productId);
    
    // Get price change count
    long getPriceChangeCount(Integer productId);
}