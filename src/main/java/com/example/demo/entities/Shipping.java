package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "\"Shipping\"")
public class Shipping {
    private Integer id;

    private CustomerOrder order;

    private String trackingNumber;

    private String shippingProvider;

    private Instant shippedDate;

    private Instant deliveredDate;

    @Id
    @Column(name = "\"Shipping_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Order_ID\"", nullable = false)
    public CustomerOrder getOrder() {
        return order;
    }

    @Size(max = 50)
    @Column(name = "\"Tracking_Number\"", length = 50)
    public String getTrackingNumber() {
        return trackingNumber;
    }

    @Size(max = 50)
    @Column(name = "\"Shipping_Provider\"", length = 50)
    public String getShippingProvider() {
        return shippingProvider;
    }

    @Column(name = "\"Shipped_Date\"")
    public Instant getShippedDate() {
        return shippedDate;
    }

    @Column(name = "\"Delivered_Date\"")
    public Instant getDeliveredDate() {
        return deliveredDate;
    }

}