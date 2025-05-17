package com.example.demo.service.impl;

import com.example.demo.entities.AuditLog;
import com.example.demo.entities.Customer;
import com.example.demo.entities.Session;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Base64;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // Simple password hashing with SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    @Override
    @Transactional
    public User createUser(Customer customer, String email, String password, String role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use: " + email);
        }
        
        User user = new User();
        user.setCustomer(customer);
        user.setEmail(email);
        user.setPasswordHash(hashPassword(password));
        user.setRole(role);
        user.setFailedLogins(0);
        user.setCreatedAt(Instant.now());
        user.setAuditLogs(new HashSet<>());
        user.setSessions(new HashSet<>());
        
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> getUsersByCustomer(Customer customer) {
        return userRepository.findByCustomer(customer);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUserEmail(Integer userId, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email is already in use: " + newEmail);
        }
        
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEmail(newEmail);
            return userRepository.save(user);
        }
        
        throw new IllegalArgumentException("User not found with ID: " + userId);
    }

    @Override
    @Transactional
    public User updateUserPassword(Integer userId, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPasswordHash(hashPassword(newPassword));
            return userRepository.save(user);
        }
        
        throw new IllegalArgumentException("User not found with ID: " + userId);
    }

    @Override
    @Transactional
    public User updateUserRole(Integer userId, String newRole) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRole(newRole);
            return userRepository.save(user);
        }
        
        throw new IllegalArgumentException("User not found with ID: " + userId);
    }

    @Override
    public Set<Session> getUserSessions(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        return optionalUser.map(User::getSessions).orElse(new HashSet<>());
    }

    @Override
    public Set<AuditLog> getUserAuditLogs(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        return optionalUser.map(User::getAuditLogs).orElse(new HashSet<>());
    }

    @Override
    @Transactional
    public User lockUserAccount(Integer userId, Instant lockedUntil) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLockedUntil(lockedUntil);
            return userRepository.save(user);
        }
        
        throw new IllegalArgumentException("User not found with ID: " + userId);
    }

    @Override
    @Transactional
    public User unlockUserAccount(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLockedUntil(null);
            user.setFailedLogins(0);
            return userRepository.save(user);
        }
        
        throw new IllegalArgumentException("User not found with ID: " + userId);
    }

    @Override
    @Transactional
    public User recordFailedLoginAttempt(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFailedLogins(user.getFailedLogins() + 1);
            
            // Auto-lock account after 5 consecutive failed attempts (for 1 hour)
            if (user.getFailedLogins() >= 5) {
                user.setLockedUntil(Instant.now().plusSeconds(3600));
            }
            
            return userRepository.save(user);
        }
        
        throw new IllegalArgumentException("User not found with ID: " + userId);
    }

    @Override
    @Transactional
    public User resetFailedLoginAttempts(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFailedLogins(0);
            return userRepository.save(user);
        }
        
        throw new IllegalArgumentException("User not found with ID: " + userId);
    }

    @Override
    public boolean isUserLocked(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Instant now = Instant.now();
            return user.getLockedUntil() != null && now.isBefore(user.getLockedUntil());
        }
        
        throw new IllegalArgumentException("User not found with ID: " + userId);
    }

    @Override
    public List<User> getUsersWithActiveSessions() {
        return userRepository.findUsersWithActiveSessions(Instant.now());
    }

    @Override
    public List<User> getLockedUsers() {
        return userRepository.findByLockedUntilGreaterThan(Instant.now());
    }

    @Override
    public List<User> getUsersWithFailedLoginAttempts(Integer threshold) {
        return userRepository.findByFailedLoginsGreaterThan(threshold);
    }

    @Override
    public List<User> getRecentlyCreatedUsers(Instant since) {
        return userRepository.findByCreatedAtAfter(since);
    }

    @Override
    public List<User> searchUsersByEmailKeyword(String emailKeyword) {
        return userRepository.findByEmailContaining(emailKeyword);
    }

    @Override
    public long countUsersByRole(String role) {
        return userRepository.countByRole(role);
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> getUsersWithAuditLogs() {
        return userRepository.findUsersWithAuditLogs();
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
    
    // Method to verify password
    public boolean verifyPassword(String rawPassword, String storedHash) {
        String hashedInput = hashPassword(rawPassword);
        return hashedInput.equals(storedHash);
    }
}