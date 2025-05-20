package com.example.demo.service.impl;

import com.example.demo.entities.PasswordResetToken;
import com.example.demo.entities.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repositories.PasswordResetTokenRepository;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    @Value("${app.password-reset.token-expiration-ms}")
    private Long tokenExpirationMs;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public PasswordResetToken createPasswordResetToken(User user) {
        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByUser(user);
        
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(tokenExpirationMs));
        
        return passwordResetTokenRepository.save(token);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);
        
        if (passwordResetToken.isEmpty()) {
            return false;
        }
        
        if (passwordResetToken.get().getExpiryDate().compareTo(Instant.now()) < 0) {
            passwordResetTokenRepository.delete(passwordResetToken.get());
            return false;
        }
        
        return true;
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }

    @Override
    public User getUserByPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .map(PasswordResetToken::getUser)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired password reset token"));
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = getUserByPasswordResetToken(token);
        userService.updateUserPassword(user.getId(), newPassword);
        passwordResetTokenRepository.deleteByUser(user);
    }
} 