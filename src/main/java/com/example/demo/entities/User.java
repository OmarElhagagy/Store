package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "\"Users\"")
public class User {
    private Integer id;

    private Customer customer;

    private String email;

    private String passwordHash;

    private String role;

    private Integer failedLogins;

    private Instant lockedUntil;

    private Instant createdAt;

    private Set<AuditLog> auditLogs = new LinkedHashSet<>();

    private Set<Session> sessions = new LinkedHashSet<>();

    @Id
    @Column(name = "\"User_ID\"", nullable = false)
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

    @Size(max = 255)
    @NotNull
    @Column(name = "\"Email\"", nullable = false)
    public String getEmail() {
        return email;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "\"Password_Hash\"", nullable = false)
    public String getPasswordHash() {
        return passwordHash;
    }

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'Registered'")
    @Column(name = "\"Role\"", nullable = false, length = 20)
    public String getRole() {
        return role;
    }

    @NotNull
    @ColumnDefault("0")
    @Column(name = "\"Failed_Logins\"", nullable = false)
    public Integer getFailedLogins() {
        return failedLogins;
    }

    @Column(name = "\"Locked_Until\"")
    public Instant getLockedUntil() {
        return lockedUntil;
    }

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"Created_At\"", nullable = false)
    public Instant getCreatedAt() {
        return createdAt;
    }

    @OneToMany(mappedBy = "user")
    public Set<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    @OneToMany(mappedBy = "user")
    public Set<Session> getSessions() {
        return sessions;
    }

}