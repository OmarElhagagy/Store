package com.example.demo.service.impl;

import com.example.demo.entities.PriceHistory;
import com.example.demo.entities.Product;
import com.example.demo.repositories.PriceHistoryRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceHistoryServiceImpl implements PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public PriceHistory recordPriceChange(Integer productId, BigDecimal newPrice) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return null;
        }

        // Get current price to avoid duplicate records if price hasn't changed
        Optional<PriceHistory> currentPriceOpt = priceHistoryRepository
            .findCurrentPriceByProductId(productId);
            
        if (currentPriceOpt.isPresent() && 
            currentPriceOpt.get().getPrice().compareTo(newPrice) == 0) {
            return currentPriceOpt.get();
        }

        PriceHistory newPriceHistory = new PriceHistory();
        newPriceHistory.setProduct(productOpt.get());
        newPriceHistory.setPrice(newPrice.setScale(2, RoundingMode.HALF_UP));
        newPriceHistory.setChangeDate(LocalDate.now());

        return priceHistoryRepository.save(newPriceHistory);
    }

    @Override
    public List<PriceHistory> getPriceHistoryForProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return List.of();
        }
        return priceHistoryRepository.findByProductIdOrderByChangeDateDesc(productId);
    }

    @Override
    public List<PriceHistory> getPriceHistoryForProductBetweenDates(
            Integer productId, LocalDate startDate, LocalDate endDate) {
        if (!productRepository.existsById(productId)) {
            return List.of();
        }
        return priceHistoryRepository.findByProductIdAndChangeDateBetween(
                productId, startDate, endDate);
    }

    @Override
    public Optional<BigDecimal> getCurrentPrice(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return Optional.empty();
        }
        return priceHistoryRepository.findCurrentPriceByProductId(productId)
                .map(PriceHistory::getPrice);
    }

    @Override
    public Optional<BigDecimal> getPriceAtDate(Integer productId, LocalDate date) {
        if (!productRepository.existsById(productId)) {
            return Optional.empty();
        }
        return priceHistoryRepository.findPriceByProductIdAndDate(productId, date)
                .map(PriceHistory::getPrice);
    }

    @Override
    public List<PriceHistory> getSignificantPriceChanges(Integer productId, BigDecimal threshold) {
        if (!productRepository.existsById(productId)) {
            return List.of();
        }
        return priceHistoryRepository.findByProductIdAndPriceGreaterThan(productId, threshold);
    }

    @Override
    public Optional<BigDecimal> getAveragePrice(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return Optional.empty();
        }
        return priceHistoryRepository.calculateAveragePrice(productId)
                .map(avg -> avg.setScale(2, RoundingMode.HALF_UP));
    }

    @Override
    public long getPriceChangeCount(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return 0L;
        }
        return priceHistoryRepository.countByProductId(productId);
    }
}