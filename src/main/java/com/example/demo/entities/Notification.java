package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"Notification\"")
public class Notification {
    private Integer id;

    private Customer customer;

    private Store store;

    private Product product;

    private Employee employee;

    private String message;

    private LocalDate sentDate;

    @Id
    @Column(name = "\"Notification_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Customer_ID\"")
    public Customer getCustomer() {
        return customer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Store_ID\"")
    public Store getStore() {
        return store;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Product_ID\"")
    public Product getProduct() {
        return product;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Employee_ID\"")
    public Employee getEmployee() {
        return employee;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "\"Message\"", nullable = false)
    public String getMessage() {
        return message;
    }

    @NotNull
    @ColumnDefault("CURRENT_DATE")
    @Column(name = "\"Sent_Date\"", nullable = false)
    public LocalDate getSentDate() {
        return sentDate;
    }

    public void setRead(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRead'");
    }

}