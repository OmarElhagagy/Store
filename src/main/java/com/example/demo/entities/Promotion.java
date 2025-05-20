package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Promotions\"")
public class Promotion {
    private Integer id;

    private String code;

    private String description;

    private BigDecimal discountPercentage;

    private BigDecimal discountAmount;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isActive = false;

    private Integer maxUses;

    private Set<CustomerOrder> customerOrders = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Promotion_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @Size(max = 20)
    @Column(name = "\"Code\"", length = 20)
    public String getCode() {
        return code;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "\"Description\"", nullable = false)
    public String getDescription() {
        return description;
    }

    @Column(name = "\"Discount_Percentage\"", precision = 5, scale = 2)
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    @Column(name = "\"Discount_Amount\"", precision = 10, scale = 2)
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    @NotNull
    @Column(name = "\"Start_Date\"", nullable = false)
    public LocalDate getStartDate() {
        return startDate;
    }

    @NotNull
    @Column(name = "\"End_Date\"", nullable = false)
    public LocalDate getEndDate() {
        return endDate;
    }

    @NotNull
    @ColumnDefault("true")
    @Column(name = "\"Is_Active\"", nullable = false)
    public Boolean getIsActive() {
        return isActive;
    }

    @Column(name = "\"Max_Uses\"")
    public Integer getMaxUses() {
        return maxUses;
    }

    @OneToMany(mappedBy = "promotion")
    public Set<CustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    public BigDecimal getDiscountRate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDiscountRate'");
    }

}