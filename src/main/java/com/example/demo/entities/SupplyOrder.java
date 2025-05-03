package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"Supply_Order\"")
public class SupplyOrder {
    private Integer id;

    private Supplier supplier;

    private Product product;

    private Employee employee;

    private Integer quantityOrdered;

    private BigDecimal price;

    private LocalDate orderDate;

    @Id
    @Column(name = "\"Supply_Order_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"Supplier_ID\"", nullable = false)
    public Supplier getSupplier() {
        return supplier;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"Product_ID\"", nullable = false)
    public Product getProduct() {
        return product;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Employee_ID\"")
    public Employee getEmployee() {
        return employee;
    }

    @NotNull
    @Column(name = "\"Quantity_Ordered\"", nullable = false)
    public Integer getQuantityOrdered() {
        return quantityOrdered;
    }

    @NotNull
    @Column(name = "\"Price\"", nullable = false, precision = 10, scale = 2)
    public BigDecimal getPrice() {
        return price;
    }

    @NotNull
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "\"Order_Date\"", nullable = false)
    public LocalDate getOrderDate() {
        return orderDate;
    }

}