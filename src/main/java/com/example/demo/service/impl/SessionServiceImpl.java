package com.example.demo.service.impl;

import com.example.demo.entities.Session;
import com.example.demo.entities.User;
import com.example.demo.repositories.SessionRepository;
import com.example.demo.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Autowired
    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    @Transactional
    public Session createSession(User user, long expirationTimeInSeconds) {
        Session session = new Session();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUser(user);
        session.setCreatedAt(Instant.now());
        session.setExpiresAt(Instant.now().plusSeconds(expirationTimeInSeconds));
        
        return sessionRepository.save(session);
    }

    @Override
    public Optional<Session> findSessionById(String sessionId) {
        return sessionRepository.findById(sessionId);
    }

    @Override
    public Optional<Session> findValidSession(String sessionId) {
        return sessionRepository.findValidSession(sessionId, Instant.now());
    }

    @Override
    public List<Session> getSessionsByUser(User user) {
        return sessionRepository.findByUser(user);
    }

    @Override
    public List<Session> getActiveSessionsByUser(User user) {
        return sessionRepository.findByUserAndExpiresAtAfter(user, Instant.now());
    }

    @Override
    @Transactional
    public Session extendSession(String sessionId, long extensionTimeInSeconds) {
        Optional<Session> optionalSession = sessionRepository.findById(sessionId);
        
        if (optionalSession.isPresent()) {
            Session session = optionalSession.get();
            session.setExpiresAt(Instant.now().plusSeconds(extensionTimeInSeconds));
            return sessionRepository.save(session);
        }
        
        throw new IllegalArgumentException("Session not found with ID: " + sessionId);
    }

    @Override
    @Transactional
    public void invalidateSession(String sessionId) {
        Optional<Session> optionalSession = sessionRepository.findById(sessionId);
        
        if (optionalSession.isPresent()) {
            Session session = optionalSession.get();
            session.setExpiresAt(Instant.now().minusSeconds(1)); // Set to expired
            sessionRepository.save(session);
        }
    }

    @Override
    @Transactional
    public void cleanupExpiredSessions() {
        sessionRepository.deleteByExpiresAtBefore(Instant.now());
    }

    @Override
    public boolean isSessionValid(String sessionId) {
        return findValidSession(sessionId).isPresent();
    }
}