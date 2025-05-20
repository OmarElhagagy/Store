package com.example.demo.service;

import com.example.demo.entities.PasswordResetToken;
import com.example.demo.entities.User;

import java.util.Optional;

public interface PasswordResetService {
    PasswordResetToken createPasswordResetToken(User user);
    
    Optional<PasswordResetToken> findByToken(String token);
    
    boolean validatePasswordResetToken(String token);
    
    void deleteExpiredTokens();
    
    User getUserByPasswordResetToken(String token);
    
    void resetPassword(String token, String newPassword);
} 