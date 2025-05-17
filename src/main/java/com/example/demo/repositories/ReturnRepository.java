package com.example.demo.repositories;

import com.example.demo.entities.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<Return, Integer> {

    // Find returns by order
    List<Return> findByOrderId(Integer orderId);
    
    // Find returns by product
    List<Return> findByProductId(Integer productId);
    
    // Find returns by date range
    List<Return> findByReturnDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find returns by reason containing (case-insensitive)
    @Query("SELECT r FROM Return r WHERE LOWER(r.reason) LIKE LOWER(CONCAT('%', :reason, '%'))")
    List<Return> findByReasonContaining(@Param("reason") String reason);
    
    // Calculate total refund amount for a product
    @Query("SELECT COALESCE(SUM(r.refundedAmount), 0) FROM Return r WHERE r.product.id = :productId")
    BigDecimal calculateTotalRefundsForProduct(@Param("productId") Integer productId);
    
    // Count returns by product
    long countByProductId(Integer productId);
    
    // Find returns with high refund amounts
    @Query("SELECT r FROM Return r WHERE r.refundedAmount >= :amount")
    List<Return> findHighValueReturns(@Param("amount") BigDecimal amount);
}