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
@Table(name = "\"Supplier\"")
public class Supplier {
    private Integer id;

    private String supplierName;

    private String email;

    private Set<PhoneNumbersSupplier> phoneNumbersSuppliers = new LinkedHashSet<>();

    private Set<Product> products = new LinkedHashSet<>();

    private Set<SupplyOrder> supplyOrders = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Supplier_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @Size(max = 100)
    @NotNull
    @Column(name = "\"Supplier_Name\"", nullable = false, length = 100)
    public String getSupplierName() {
        return supplierName;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "\"Email\"", nullable = false)
    public String getEmail() {
        return email;
    }

    @OneToMany(mappedBy = "supplier")
    public Set<PhoneNumbersSupplier> getPhoneNumbersSuppliers() {
        return phoneNumbersSuppliers;
    }

    @OneToMany(mappedBy = "supplier")
    public Set<Product> getProducts() {
        return products;
    }

    @OneToMany(mappedBy = "supplier")
    public Set<SupplyOrder> getSupplyOrders() {
        return supplyOrders;
    }

}