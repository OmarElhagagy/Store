package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Payment_Methods\"")
public class PaymentMethod {
    private Integer id;

    private String methodName;

    private Set<CustomerOrder> customerOrders = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Payment_Method_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "\"Method_Name\"", nullable = false, length = 50)
    public String getMethodName() {
        return methodName;
    }

    @OneToMany(mappedBy = "paymentMethod")
    public Set<CustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

}