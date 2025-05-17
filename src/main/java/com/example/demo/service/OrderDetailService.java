package com.example.demo.service;

import com.example.demo.entities.OrderDetail;
import java.math.BigDecimal;
import java.util.List;

public interface OrderDetailService {

    // Create or update an order detail
    OrderDetail saveOrderDetail(OrderDetail orderDetail);

    // Get all order details for an order
    List<OrderDetail> getOrderDetailsByOrder(Integer orderId);

    // Get all order details for a product
    List<OrderDetail> getOrderDetailsByProduct(Integer productId);

    // Get specific order detail
    OrderDetail getOrderDetail(Integer orderId, Integer productId);

    // Update quantity for an order detail
    OrderDetail updateQuantity(Integer orderId, Integer productId, Integer quantity);

    // Delete an order detail
    void deleteOrderDetail(Integer orderId, Integer productId);

    // Delete all order details for an order
    void deleteAllOrderDetailsForOrder(Integer orderId);

    // Get total quantity sold for a product
    Integer getTotalQuantitySoldForProduct(Integer productId);

    // Get total revenue for a product
    BigDecimal getTotalRevenueForProduct(Integer productId);
}