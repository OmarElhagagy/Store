package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"Cart\"")
public class Cart {
    private Integer id;

    private Customer customer;

    private Product product;

    private Integer quantity;

    private LocalDate addedDate;

    @Id
    @Column(name = "\"Cart_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Customer_ID\"", nullable = false)
    public Customer getCustomer() {
        return customer;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
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
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "\"Added_Date\"", nullable = false)
    public LocalDate getAddedDate() {
        return addedDate;
    }

}