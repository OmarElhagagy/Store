package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Customer\"")
public class Customer {
    private Integer id;

    private String fName;

    private String mName;

    private String lName;

    private String gender;

    private LocalDate birthDate;

    private String email;

    private Set<Address> addresses = new LinkedHashSet<>();

    private Set<Cart> carts = new LinkedHashSet<>();

    private Set<CustomerOrder> customerOrders = new LinkedHashSet<>();

    private Set<Notification> notifications = new LinkedHashSet<>();

    private Set<PhoneNumbersCustomer> phoneNumbersCustomers = new LinkedHashSet<>();

    private Set<Review> reviews = new LinkedHashSet<>();

    private Set<User> users = new LinkedHashSet<>();

    private Set<Wishlist> wishlists = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Customer_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    public String getFName() {
        return fName;
    }

    public String getMName() {
        return mName;
    }

    public String getLName() {
        return lName;
    }

    @Column(name = "\"Active\"", nullable = false)
    private boolean active = true;

    @NotNull
    @Column(name = "\"Gender\"", nullable = false, length = Integer.MAX_VALUE)
    public String getGender() {
        return gender;
    }

    @NotNull
    @Column(name = "\"Birth_Date\"", nullable = false)
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "\"Email\"", nullable = false)
    public String getEmail() {
        return email;
    }

    @OneToMany(mappedBy = "customer")
    public Set<Address> getAddresses() {
        return addresses;
    }

    @OneToMany(mappedBy = "customer")
    public Set<Cart> getCarts() {
        return carts;
    }

    @OneToMany(mappedBy = "customer")
    public Set<CustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    @OneToMany(mappedBy = "customer")
    public Set<Notification> getNotifications() {
        return notifications;
    }

    @OneToMany(mappedBy = "customer")
    public Set<PhoneNumbersCustomer> getPhoneNumbersCustomers() {
        return phoneNumbersCustomers;
    }

    @OneToMany(mappedBy = "customer")
    public Set<Review> getReviews() {
        return reviews;
    }

    @OneToMany(mappedBy = "customer")
    public Set<User> getUsers() {
        return users;
    }

    @OneToMany(mappedBy = "customer")
    public Set<Wishlist> getWishlists() {
        return wishlists;
    }

    // Add getter and setter for Active
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
