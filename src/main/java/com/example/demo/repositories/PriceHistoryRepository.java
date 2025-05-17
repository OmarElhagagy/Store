package com.example.demo.repositories;

import com.example.demo.entities.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Integer> {

    // Find all price changes for a product
    List<PriceHistory> findByProductIdOrderByChangeDateDesc(Integer productId);
    
    // Find price changes within date range
    List<PriceHistory> findByProductIdAndChangeDateBetween(Integer productId, LocalDate startDate, LocalDate endDate);
    
    // Get current price (most recent)
    @Query("SELECT ph FROM PriceHistory ph WHERE ph.product.id = :productId ORDER BY ph.changeDate DESC LIMIT 1")
    Optional<PriceHistory> findCurrentPriceByProductId(@Param("productId") Integer productId);
    
    // Get price at specific date
    @Query("SELECT ph FROM PriceHistory ph " +
           "WHERE ph.product.id = :productId AND ph.changeDate <= :date " +
           "ORDER BY ph.changeDate DESC LIMIT 1")
    Optional<PriceHistory> findPriceByProductIdAndDate(
            @Param("productId") Integer productId,
            @Param("date") LocalDate date);
    
    // Get price changes greater than specific amount
    List<PriceHistory> findByProductIdAndPriceGreaterThan(Integer productId, BigDecimal price);
    
    // Get average price for product
    @Query("SELECT AVG(ph.price) FROM PriceHistory ph WHERE ph.product.id = :productId")
    Optional<BigDecimal> calculateAveragePrice(@Param("productId") Integer productId);
    
    // Get price history count for product
    long countByProductId(Integer productId);
}