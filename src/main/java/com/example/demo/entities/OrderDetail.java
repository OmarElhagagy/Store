package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "\"Order_Details\"")
public class OrderDetail {
    private OrderDetailId id;

    private CustomerOrder order;

    private Product product;

    private Integer quantity;

    private BigDecimal originalPrice;

    private BigDecimal discountAmount;

    private BigDecimal finalPrice;

    @EmbeddedId
    public OrderDetailId getId() {
        return id;
    }

    @MapsId("orderId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Order_ID\"", nullable = false)
    public CustomerOrder getOrder() {
        return order;
    }

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"Product_ID\"", nullable = false)
    public Product getProduct() {
        return product;
    }

    @NotNull
    @Column(name = "\"Quantity\"", nullable = false)
    public Integer getQuantity() {
        return quantity;
    }

    @NotNull
    @Column(name = "\"Original_Price\"", nullable = false, precision = 10, scale = 2)
    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    @NotNull
    @Column(name = "\"Discount_Amount\"", nullable = false, precision = 10, scale = 2)
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    @NotNull
    @Column(name = "\"Final_Price\"", nullable = false, precision = 10, scale = 2)
    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public BigDecimal getPrice() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPrice'");
    }

    public BigDecimal getUnitPrice() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUnitPrice'");
    }

}