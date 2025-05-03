package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Addresses\"")
public class Address {
    private Integer id;

    private Customer customer;

    private String street;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private Boolean isDefault = false;

    private Set<CustomerOrder> customerOrders = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Address_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"Customer_ID\"", nullable = false)
    public Customer getCustomer() {
        return customer;
    }

    @Size(max = 100)
    @NotNull
    @Column(name = "\"Street\"", nullable = false, length = 100)
    public String getStreet() {
        return street;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "\"City\"", nullable = false, length = 50)
    public String getCity() {
        return city;
    }

    @Size(max = 50)
    @Column(name = "\"State\"", length = 50)
    public String getState() {
        return state;
    }

    @Size(max = 20)
    @NotNull
    @Column(name = "\"Postal_Code\"", nullable = false, length = 20)
    public String getPostalCode() {
        return postalCode;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "\"Country\"", nullable = false, length = 50)
    public String getCountry() {
        return country;
    }

    @NotNull
    @ColumnDefault("false")
    @Column(name = "\"Is_Default\"", nullable = false)
    public Boolean getIsDefault() {
        return isDefault;
    }

    @OneToMany(mappedBy = "address")
    public Set<CustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
