package com.example.demo.repositories;

import com.example.demo.entities.PasswordResetToken;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);
    
    @Modifying
    int deleteByUser(User user);
    
    @Modifying
    int deleteByExpiryDateBefore(Instant now);
} 