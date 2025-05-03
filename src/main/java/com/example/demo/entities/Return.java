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

@Getter
@Setter
@Entity
@Table(name = "\"Return\"")
public class Return {
    private Integer id;

    private CustomerOrder order;

    private Product product;

    private BigDecimal refundedAmount;

    private Integer quantityReturned;

    private String reason;

    private LocalDate returnDate;

    @Id
    @Column(name = "\"Return_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"Order_ID\"", nullable = false)
    public CustomerOrder getOrder() {
        return order;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"Product_ID\"", nullable = false)
    public Product getProduct() {
        return product;
    }

    @NotNull
    @Column(name = "\"Refunded_Amount\"", nullable = false, precision = 10, scale = 2)
    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    @NotNull
    @Column(name = "\"Quantity_Returned\"", nullable = false)
    public Integer getQuantityReturned() {
        return quantityReturned;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "\"Reason\"", nullable = false)
    public String getReason() {
        return reason;
    }

    @NotNull
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "\"Return_Date\"", nullable = false)
    public LocalDate getReturnDate() {
        return returnDate;
    }

}