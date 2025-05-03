package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "\"Phone_Numbers_Customers\"")
public class PhoneNumbersCustomer {
    private String phoneNumber;

    private Customer customer;

    @Id
    @Size(max = 20)
    @Column(name = "\"Phone_Number\"", nullable = false, length = 20)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Customer_ID\"", nullable = false)
    public Customer getCustomer() {
        return customer;
    }

}