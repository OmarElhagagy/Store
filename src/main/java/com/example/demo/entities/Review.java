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
@Table(name = "\"Reviews\"")
public class Review {
    private Integer id;

    private Product product;

    private Customer customer;

    private Integer rating;

    private String comment;

    private Instant reviewDate;

    @Id
    @Column(name = "\"Review_ID\"", nullable = false)
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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Customer_ID\"", nullable = false)
    public Customer getCustomer() {
        return customer;
    }

    @NotNull
    @Column(name = "\"Rating\"", nullable = false)
    public Integer getRating() {
        return rating;
    }

    @Column(name = "\"Comment\"", length = Integer.MAX_VALUE)
    public String getComment() {
        return comment;
    }

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"Review_Date\"", nullable = false)
    public Instant getReviewDate() {
        return reviewDate;
    }

}