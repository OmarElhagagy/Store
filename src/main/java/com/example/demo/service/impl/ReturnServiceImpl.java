package com.example.demo.service.impl;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.ReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final ReturnRepository returnRepository;
    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional
    public Return createReturn(Return returnRequest) {
        // Validate order exists
        if (!orderRepository.existsById(returnRequest.getOrder().getId())) {
            return null;
        }

        // Validate product exists
        if (!productRepository.existsById(returnRequest.getProduct().getId())) {
            return null;
        }

        // Validate product was in the order
        Optional<OrderDetail> orderDetail = orderDetailRepository
            .findByOrderIdAndProductId(
                returnRequest.getOrder().getId(),
                returnRequest.getProduct().getId()
            );
        
        if (orderDetail.isEmpty()) {
            return null;
        }

        // Validate quantity doesn't exceed ordered quantity
        if (returnRequest.getQuantityReturned() <= 0 ||
            returnRequest.getQuantityReturned() > orderDetail.get().getQuantity()) {
            return null;
        }

        // Set default return date if not provided
        if (returnRequest.getReturnDate() == null) {
            returnRequest.setReturnDate(LocalDate.now());
        }

        // Calculate refund amount if not provided
        if (returnRequest.getRefundedAmount() == null) {
            BigDecimal unitPrice = orderDetail.get().getUnitPrice();
            returnRequest.setRefundedAmount(
                unitPrice.multiply(BigDecimal.valueOf(returnRequest.getQuantityReturned()))
            );
        }

        return returnRepository.save(returnRequest);
    }

    @Override
    public Return getReturnById(Integer id) {
        return returnRepository.findById(id).orElse(null);
    }

    @Override
    public List<Return> getReturnsByOrder(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            return List.of();
        }
        return returnRepository.findByOrderId(orderId);
    }

    @Override
    public List<Return> getReturnsByProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return List.of();
        }
        return returnRepository.findByProductId(productId);
    }

    @Override
    public List<Return> getReturnsByDateRange(LocalDate startDate, LocalDate endDate) {
        return returnRepository.findByReturnDateBetween(startDate, endDate);
    }

    @Override
    public List<Return> searchReturnsByReason(String reason) {
        return returnRepository.findByReasonContaining(reason);
    }

    @Override
    public BigDecimal getTotalRefundsForProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return BigDecimal.ZERO;
        }
        return returnRepository.calculateTotalRefundsForProduct(productId);
    }

    @Override
    public long getReturnCountForProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return 0L;
        }
        return returnRepository.countByProductId(productId);
    }

    @Override
    public List<Return> getHighValueReturns(BigDecimal minAmount) {
        return returnRepository.findHighValueReturns(minAmount);
    }

    @Override
    @Transactional
    public void deleteReturn(Integer id) {
        returnRepository.deleteById(id);
    }
}