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

@Getter
@Setter
@Entity
@Table(name = "\"Audit_Log\"")
public class AuditLog {
    private Integer id;

    private User user;

    private Employee employee;

    private String action;

    private String tableName;

    private Integer recordId;

    private Instant timestamp;

    @Id
    @Column(name = "\"Log_ID\"", nullable = false)
    public Integer getId() {
        return id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"User_ID\"")
    public User getUser() {
        return user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "\"Employee_ID\"")
    public Employee getEmployee() {
        return employee;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "\"Action\"", nullable = false, length = 50)
    public String getAction() {
        return action;
    }

    @Size(max = 50)
    @Column(name = "\"Table_Name\"", length = 50)
    public String getTableName() {
        return tableName;
    }

    @Column(name = "\"Record_ID\"")
    public Integer getRecordId() {
        return recordId;
    }

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"Timestamp\"", nullable = false)
    public Instant getTimestamp() {
        return timestamp;
    }

}