package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Customer\"")
public class Customer {
    private Integer id;
    
    private String firstName;
    
    private String middleName;
    
    private String lastName;
    
    private String gender;

    private LocalDate birthDate;

    private String email;
    
    private String phone;
    
    private Instant createdAt;
    
    @Column(name = "\"Active\"", nullable = false)
    private boolean active = true;

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

    @Column(name = "\"First_Name\"")
    public String getFirstName() {
        return firstName;
    }

    @Column(name = "\"Middle_Name\"")
    public String getMiddleName() {
        return middleName;
    }

    @Column(name = "\"Last_Name\"")
    public String getLastName() {
        return lastName;
    }

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
    
    @Column(name = "\"Phone\"")
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    @Column(name = "\"Created_At\"")
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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

    // For backward compatibility with existing code
    public String getFName() {
        return firstName;
    }

    public String getMName() {
        return middleName;
    }

    public String getLName() {
        return lastName;
    }

    public boolean isActive() {
        return active;
    }
}
