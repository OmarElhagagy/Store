package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "\"Reserved_Stock\"")
public class ReservedStock {
    private Integer id;

    private Store store;

    private Product product;

    private Integer quantity;

    private Instant reservedAt;

    private Instant expiresAt;

    @Id
    @Column(name = "\"Reservation_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Store_ID\"", nullable = false)
    public Store getStore() {
        return store;
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
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"Reserved_At\"", nullable = false)
    public Instant getReservedAt() {
        return reservedAt;
    }

    @NotNull
    @Column(name = "\"Expires_At\"", nullable = false)
    public Instant getExpiresAt() {
        return expiresAt;
    }

}