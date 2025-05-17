package com.example.demo.service;

import com.example.demo.entities.Session;
import com.example.demo.entities.User;


import java.util.List;
import java.util.Optional;

public interface SessionService {
    
    // Create a new session
    Session createSession(User user, long expirationTimeInSeconds);
    
    // Find session by ID
    Optional<Session> findSessionById(String sessionId);
    
    // Find valid session (not expired)
    Optional<Session> findValidSession(String sessionId);
    
    // Get all sessions for a user
    List<Session> getSessionsByUser(User user);
    
    // Get active sessions for a user
    List<Session> getActiveSessionsByUser(User user);
    
    // Extend session expiration
    Session extendSession(String sessionId, long extensionTimeInSeconds);
    
    // Invalidate a session
    void invalidateSession(String sessionId);
    
    // Clean up expired sessions
    void cleanupExpiredSessions();
    
    // Check if session is valid
    boolean isSessionValid(String sessionId);
}