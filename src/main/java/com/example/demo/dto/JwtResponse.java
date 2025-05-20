package com.example.demo.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Integer id;
    private String email;
    private String role;

    public JwtResponse(String token, String refreshToken, Integer id, String email, String role) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.id = id;
        this.email = email;
        this.role = role;
    }
} 