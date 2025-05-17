package com.example.demo.service.impl;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderDetail saveOrderDetail(OrderDetail orderDetail) {
        // Validate order exists
        if (!orderRepository.existsById(orderDetail.getOrder().getId())) {
            return null;
        }

        // Validate product exists
        if (!productRepository.existsById(orderDetail.getProduct().getId())) {
            return null;
        }

        // Calculate prices if not set
        if (orderDetail.getOriginalPrice() == null) {
            Product product = productRepository.getReferenceById(orderDetail.getProduct().getId());
            orderDetail.setOriginalPrice(product.getPrice());
        }

        if (orderDetail.getDiscountAmount() == null) {
            orderDetail.setDiscountAmount(BigDecimal.ZERO);
        }

        if (orderDetail.getFinalPrice() == null) {
            orderDetail.setFinalPrice(
                orderDetail.getOriginalPrice()
                    .subtract(orderDetail.getDiscountAmount())
                    .multiply(BigDecimal.valueOf(orderDetail.getQuantity()))
            );
        }

        // Set composite ID
        OrderDetailId id = new OrderDetailId();
        id.setOrderId(orderDetail.getOrder().getId());
        id.setProductId(orderDetail.getProduct().getId());
        orderDetail.setId(id);

        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrder(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            return List.of();
        }
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderDetail> getOrderDetailsByProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return List.of();
        }
        return orderDetailRepository.findByProductId(productId);
    }

    @Override
    public OrderDetail getOrderDetail(Integer orderId, Integer productId) {
        return orderDetailRepository.findByOrderIdAndProductId(orderId, productId).orElse(null);
    }

    @Override
    @Transactional
    public OrderDetail updateQuantity(Integer orderId, Integer productId, Integer quantity) {
        Optional<OrderDetail> orderDetailOpt = orderDetailRepository.findByOrderIdAndProductId(orderId, productId);
        if (orderDetailOpt.isEmpty()) {
            return null;
        }

        orderDetailRepository.updateQuantity(orderId, productId, quantity);
        
        // Refresh the entity to get updated values
        return orderDetailRepository.findByOrderIdAndProductId(orderId, productId).orElse(null);
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Integer orderId, Integer productId) {
        orderDetailRepository.deleteByOrderIdAndProductId(orderId, productId);
    }

    @Override
    @Transactional
    public void deleteAllOrderDetailsForOrder(Integer orderId) {
        if (orderRepository.existsById(orderId)) {
            orderDetailRepository.deleteAllByOrderId(orderId);
        }
    }

    @Override
    public Integer getTotalQuantitySoldForProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return 0;
        }
        return orderDetailRepository.getTotalQuantitySoldForProduct(productId);
    }

    @Override
    public BigDecimal getTotalRevenueForProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return BigDecimal.ZERO;
        }
        return orderDetailRepository.getTotalRevenueForProduct(productId);
    }
}