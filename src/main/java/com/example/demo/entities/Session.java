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
@Table(name = "\"Sessions\"")
public class Session {
    private String sessionId;

    private User user;

    private Instant createdAt;

    private Instant expiresAt;

    @Id
    @Size(max = 128)
    @Column(name = "\"Session_ID\"", nullable = false, length = 128)
    public String getSessionId() {
        return sessionId;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "\"User_ID\"", nullable = false)
    public User getUser() {
        return user;
    }

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "\"Created_At\"", nullable = false)
    public Instant getCreatedAt() {
        return createdAt;
    }

    @NotNull
    @Column(name = "\"Expires_At\"", nullable = false)
    public Instant getExpiresAt() {
        return expiresAt;
    }

}