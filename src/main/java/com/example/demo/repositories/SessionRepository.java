package com.example.demo.repositories;

import com.example.demo.entities.Session;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    
    // Find all sessions for a specific user
    List<Session> findByUser(User user);
    
    // Find all expired sessions
    List<Session> findByExpiresAtBefore(Instant now);
    
    // Find valid session by ID
    @Query("SELECT s FROM Session s WHERE s.sessionId = ?1 AND s.expiresAt > ?2")
    Optional<Session> findValidSession(String sessionId, Instant now);
    
    // Find all active sessions for a user
    List<Session> findByUserAndExpiresAtAfter(User user, Instant now);
    
    // Delete all expired sessions
    void deleteByExpiresAtBefore(Instant now);
}