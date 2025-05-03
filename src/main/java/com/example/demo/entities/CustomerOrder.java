package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Customer_Order\"")
public class CustomerOrder {
    private Integer id;

    private Employee employeeSeller;

    private Customer customer;

    private PaymentMethod paymentMethod;

    private Address address;

    private Promotion promotion;

    private LocalDate purchaseDate;

    private BigDecimal totalPrice;

    private String status;

    private Set<OrderDetail> orderDetails = new LinkedHashSet<>();

    private Set<Return> returnFields = new LinkedHashSet<>();

    private Set<Shipping> shippings = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Order_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"Employee_Seller_ID\"", nullable = false)
    public Employee getEmployeeSeller() {
        return employeeSeller;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"Customer_ID\"", nullable = false)
    public Customer getCustomer() {
        return customer;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"Payment_Method_ID\"", nullable = false)
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Address_ID\"")
    public Address getAddress() {
        return address;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Promotion_ID\"")
    public Promotion getPromotion() {
        return promotion;
    }

    @NotNull
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "\"Purchase_Date\"", nullable = false)
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    @NotNull
    @Column(name = "\"Total_Price\"", nullable = false, precision = 10, scale = 2)
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    @Size(max = 20)
    @NotNull
    @Column(name = "\"Status\"", nullable = false, length = 20)
    public String getStatus() {
        return status;
    }

    @OneToMany(mappedBy = "order")
    public Set<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    @OneToMany(mappedBy = "order")
    public Set<Return> getReturnFields() {
        return returnFields;
    }

    @OneToMany(mappedBy = "order")
    public Set<Shipping> getShippings() {
        return shippings;
    }

}