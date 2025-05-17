package com.example.demo.repositories;

import com.example.demo.entities.OrderDetail;
import com.example.demo.entities.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {

    // Find all order details for a specific order
    List<OrderDetail> findByOrderId(Integer orderId);

    // Find all order details for a specific product
    List<OrderDetail> findByProductId(Integer productId);

    // Find specific order detail by order and product
    Optional<OrderDetail> findByOrderIdAndProductId(Integer orderId, Integer productId);

    // Calculate total quantity sold for a product
    @Query("SELECT COALESCE(SUM(od.quantity), 0) FROM OrderDetail od WHERE od.product.id = :productId")
    Integer getTotalQuantitySoldForProduct(@Param("productId") Integer productId);

    // Calculate total revenue for a product
    @Query("SELECT COALESCE(SUM(od.finalPrice), 0) FROM OrderDetail od WHERE od.product.id = :productId")
    BigDecimal getTotalRevenueForProduct(@Param("productId") Integer productId);

    // Update quantity for an order detail
    @Modifying
    @Query("UPDATE OrderDetail od SET od.quantity = :quantity WHERE od.order.id = :orderId AND od.product.id = :productId")
    void updateQuantity(@Param("orderId") Integer orderId, 
                      @Param("productId") Integer productId, 
                      @Param("quantity") Integer quantity);

    // Delete all order details for an order
    @Modifying
    @Query("DELETE FROM OrderDetail od WHERE od.order.id = :orderId")
    void deleteAllByOrderId(@Param("orderId") Integer orderId);

    void deleteByOrderIdAndProductId(Integer orderId, Integer productId);

    boolean existsByOrderCustomerIdAndProductId(Integer customerId, Integer productId);
}