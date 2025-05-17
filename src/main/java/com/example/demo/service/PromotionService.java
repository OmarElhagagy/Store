package com.example.demo.service;

import com.example.demo.entities.Promotion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PromotionService {

    // Basic CRUD operations
    Promotion createPromotion(Promotion promotion);
    Promotion updatePromotion(Integer id, Promotion promotion);
    void deletePromotion(Integer id);
    Optional<Promotion> getPromotionById(Integer id);
    List<Promotion> getAllPromotions();
    
    // Query methods
    Optional<Promotion> getPromotionByCode(String code);
    List<Promotion> getActivePromotions();
    List<Promotion> getCurrentPromotions();
    List<Promotion> getPromotionsExpiringSoon(int days);
    
    // Validation methods
    boolean isPromotionValid(String code);
    boolean isPromotionApplicable(Integer promotionId, BigDecimal orderTotal);
    
    // Usage tracking
    long getPromotionUsageCount(Integer promotionId);
    
    // Activation management
    void activatePromotion(Integer id);
    void deactivatePromotion(Integer id);
    
    // Search methods
    List<Promotion> searchPromotionsByDiscountRange(BigDecimal minValue, BigDecimal maxValue, boolean isPercentage);
}