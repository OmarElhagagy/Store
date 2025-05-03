package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Employee\"")
public class Employee {
    private Integer id;

    private Store store;

    private Storage storageNo;

    private String fName;

    private String mName;

    private String lName;

    private String email;

    private String gender;

    private String role;

    private Employee supervisor;

    private Set<AuditLog> auditLogs = new LinkedHashSet<>();

    private Set<CustomerOrder> customerOrders = new LinkedHashSet<>();

    private Set<Employee> employees = new LinkedHashSet<>();

    private Set<Notification> notifications = new LinkedHashSet<>();

    private Set<PhoneNumbersEmployee> phoneNumbersEmployees = new LinkedHashSet<>();

    private Set<SupplyOrder> supplyOrders = new LinkedHashSet<>();

    @Id
    @Column(name = "\"Employee_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "\"Store_ID\"", nullable = false)
    public Store getStore() {
        return store;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Storage_No\"")
    public Storage getStorageNo() {
        return storageNo;
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

    @Size(max = 255)
    @NotNull
    @Column(name = "\"Email\"", nullable = false)
    public String getEmail() {
        return email;
    }

    @NotNull
    @Column(name = "\"Gender\"", nullable = false, length = Integer.MAX_VALUE)
    public String getGender() {
        return gender;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "\"Role\"", nullable = false, length = 50)
    public String getRole() {
        return role;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Supervisor\"")
    public Employee getSupervisor() {
        return supervisor;
    }

    @OneToMany(mappedBy = "employee")
    public Set<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    @OneToMany(mappedBy = "employeeSeller")
    public Set<CustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    @OneToMany(mappedBy = "supervisor")
    public Set<Employee> getEmployees() {
        return employees;
    }

    @OneToMany(mappedBy = "employee")
    public Set<Notification> getNotifications() {
        return notifications;
    }

    @OneToMany(mappedBy = "employee")
    public Set<PhoneNumbersEmployee> getPhoneNumbersEmployees() {
        return phoneNumbersEmployees;
    }

    @OneToMany(mappedBy = "employee")
    public Set<SupplyOrder> getSupplyOrders() {
        return supplyOrders;
    }

}