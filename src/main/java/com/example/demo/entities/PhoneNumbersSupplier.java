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
@Table(name = "\"Phone_Numbers_Supplier\"")
public class PhoneNumbersSupplier {
    private String phoneNumber;

    private Supplier supplier;

    @Id
    @Size(max = 20)
    @Column(name = "\"Phone_Number\"", nullable = false, length = 20)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Supplier_ID\"", nullable = false)
    public Supplier getSupplier() {
        return supplier;
    }

}