package com.example.demo.repositories;

import com.example.demo.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    // Find by promotion code
    Optional<Promotion> findByCode(String code);
    
    // Find active promotions
    List<Promotion> findByIsActiveTrue();
    
    // Find promotions by date range
    List<Promotion> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate date);
    
    // Find current active promotions (within date range and active)
    @Query("SELECT p FROM Promotion p WHERE " +
           "p.isActive = true AND " +
           "p.startDate <= CURRENT_DATE AND " +
           "p.endDate >= CURRENT_DATE")
    List<Promotion> findCurrentlyActivePromotions();
    
    // Find promotions expiring soon
    List<Promotion> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Count uses of a promotion
    @Query("SELECT COUNT(o) FROM CustomerOrder o WHERE o.promotion.id = :promotionId")
    long countPromotionUses(@Param("promotionId") Integer promotionId);
    
    // Find promotions by discount amount range
    List<Promotion> findByDiscountAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    // Find promotions by discount percentage range
    List<Promotion> findByDiscountPercentageBetween(BigDecimal minPercentage, BigDecimal maxPercentage);
}