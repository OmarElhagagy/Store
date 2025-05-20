package com.example.demo.service;

import com.example.demo.entities.RefreshToken;
import com.example.demo.exception.TokenRefreshException;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Integer userId);
    
    RefreshToken verifyExpiration(RefreshToken token) throws TokenRefreshException;
    
    Optional<RefreshToken> findByToken(String token);
    
    int deleteByUserId(Integer userId);
} 