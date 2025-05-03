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
@Table(name = "\"Store\"")
public class Store {
    private Integer id;

    private String address;

    private String email;

    private Integer minStock;

    private Set<Employee> employees = new LinkedHashSet<>();

    private Set<Notification> notifications = new LinkedHashSet<>();

    private Set<ReservedStock> reservedStocks = new LinkedHashSet<>();

    private Set<Storage> storages = new LinkedHashSet<>();

    private Set<StoreInventory> storeInventories = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Store_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "\"Address\"", nullable = false)
    public String getAddress() {
        return address;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "\"Email\"", nullable = false)
    public String getEmail() {
        return email;
    }

    @NotNull
    @Column(name = "\"Min_Stock\"", nullable = false)
    public Integer getMinStock() {
        return minStock;
    }

    @OneToMany(mappedBy = "store")
    public Set<Employee> getEmployees() {
        return employees;
    }

    @OneToMany(mappedBy = "store")
    public Set<Notification> getNotifications() {
        return notifications;
    }

    @OneToMany(mappedBy = "store")
    public Set<ReservedStock> getReservedStocks() {
        return reservedStocks;
    }

    @OneToMany(mappedBy = "store")
    public Set<Storage> getStorages() {
        return storages;
    }

    @OneToMany(mappedBy = "store")
    public Set<StoreInventory> getStoreInventories() {
        return storeInventories;
    }

}