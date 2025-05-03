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
@Table(name = "\"Price_History\"")
public class PriceHistory {
    private Integer id;

    private Product product;

    private BigDecimal price;

    private LocalDate changeDate;

    @Id
    @Column(name = "\"History_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Product_ID\"", nullable = false)
    public Product getProduct() {
        return product;
    }

    @NotNull
    @Column(name = "\"Price\"", nullable = false, precision = 10, scale = 2)
    public BigDecimal getPrice() {
        return price;
    }

    @NotNull
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "\"Change_Date\"", nullable = false)
    public LocalDate getChangeDate() {
        return changeDate;
    }

}