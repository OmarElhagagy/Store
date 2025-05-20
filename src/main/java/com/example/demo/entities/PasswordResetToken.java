package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "\"PasswordResetTokens\"")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"Token_ID\"", nullable = false)
    private Integer id;

    @Column(name = "\"Token\"", nullable = false, length = 255, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"User_ID\"", nullable = false)
    private User user;

    @Column(name = "\"Expiry_Date\"", nullable = false)
    private Instant expiryDate;
} 