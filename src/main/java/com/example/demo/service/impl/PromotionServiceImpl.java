package com.example.demo.service.impl;

import com.example.demo.entities.Promotion;
import com.example.demo.repositories.PromotionRepository;
import com.example.demo.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public Promotion createPromotion(Promotion promotion) {
        // Validate code uniqueness
        if (promotion.getCode() != null && 
            promotionRepository.findByCode(promotion.getCode()).isPresent()) {
            return null;
        }

        // Set default values
        if (promotion.getIsActive() == null) {
            promotion.setIsActive(true);
        }

        return promotionRepository.save(promotion);
    }

    @Override
    @Transactional
    public Promotion updatePromotion(Integer id, Promotion promotion) {
        Optional<Promotion> existingOpt = promotionRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return null;
        }

        Promotion existing = existingOpt.get();

        // Update fields
        if (promotion.getDescription() != null) {
            existing.setDescription(promotion.getDescription());
        }
        if (promotion.getDiscountPercentage() != null) {
            existing.setDiscountPercentage(promotion.getDiscountPercentage());
        }
        if (promotion.getDiscountAmount() != null) {
            existing.setDiscountAmount(promotion.getDiscountAmount());
        }
        if (promotion.getStartDate() != null) {
            existing.setStartDate(promotion.getStartDate());
        }
        if (promotion.getEndDate() != null) {
            existing.setEndDate(promotion.getEndDate());
        }
        if (promotion.getMaxUses() != null) {
            existing.setMaxUses(promotion.getMaxUses());
        }

        // Handle code update
        if (promotion.getCode() != null && 
            !promotion.getCode().equals(existing.getCode())) {
            if (promotionRepository.findByCode(promotion.getCode()).isPresent()) {
                return null; // Code already exists
            }
            existing.setCode(promotion.getCode());
        }

        return promotionRepository.save(existing);
    }

    @Override
    @Transactional
    public void deletePromotion(Integer id) {
        promotionRepository.deleteById(id);
    }

    @Override
    public Optional<Promotion> getPromotionById(Integer id) {
        return promotionRepository.findById(id);
    }

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Optional<Promotion> getPromotionByCode(String code) {
        return promotionRepository.findByCode(code);
    }

    @Override
    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActiveTrue();
    }

    @Override
    public List<Promotion> getCurrentPromotions() {
        return promotionRepository.findCurrentlyActivePromotions();
    }

    @Override
    public List<Promotion> getPromotionsExpiringSoon(int days) {
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusDays(days);
        return promotionRepository.findByEndDateBetween(now, endDate);
    }

    @Override
    public boolean isPromotionValid(String code) {
        Optional<Promotion> promotionOpt = promotionRepository.findByCode(code);
        if (promotionOpt.isEmpty()) {
            return false;
        }

        Promotion promotion = promotionOpt.get();
        LocalDate today = LocalDate.now();

        return promotion.getIsActive() &&
               !today.isBefore(promotion.getStartDate()) &&
               !today.isAfter(promotion.getEndDate()) &&
               (promotion.getMaxUses() == null || 
                promotionRepository.countPromotionUses(promotion.getId()) < promotion.getMaxUses());
    }

    @Override
    public boolean isPromotionApplicable(Integer promotionId, BigDecimal orderTotal) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
        if (promotionOpt.isEmpty()) {
            return false;
        }

        Promotion promotion = promotionOpt.get();
        if (promotion.getDiscountAmount() != null) {
            return orderTotal.compareTo(promotion.getDiscountAmount()) > 0;
        }
        return true;
    }

    @Override
    public long getPromotionUsageCount(Integer promotionId) {
        return promotionRepository.countPromotionUses(promotionId);
    }

    @Override
    @Transactional
    public void activatePromotion(Integer id) {
        promotionRepository.findById(id).ifPresent(promotion -> {
            promotion.setIsActive(true);
            promotionRepository.save(promotion);
        });
    }

    @Override
    @Transactional
    public void deactivatePromotion(Integer id) {
        promotionRepository.findById(id).ifPresent(promotion -> {
            promotion.setIsActive(false);
            promotionRepository.save(promotion);
        });
    }

    @Override
    public List<Promotion> searchPromotionsByDiscountRange(BigDecimal minValue, BigDecimal maxValue, boolean isPercentage) {
        if (isPercentage) {
            return promotionRepository.findByDiscountPercentageBetween(minValue, maxValue);
        } else {
            return promotionRepository.findByDiscountAmountBetween(minValue, maxValue);
        }
    }
}