package com.example.demo.service;

import com.example.demo.entities.AuditLog;
import com.example.demo.entities.Customer;
import com.example.demo.entities.Session;
import com.example.demo.entities.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    
    // Create a new user
    User createUser(Customer customer, String email, String password, String role);
    
    // Find user by ID
    Optional<User> findUserById(Integer id);
    
    // Find user by email
    Optional<User> findUserByEmail(String email);
    
    // Get all users
    List<User> getAllUsers();
    
    // Get users by role
    List<User> getUsersByRole(String role);
    
    // Get users by customer
    List<User> getUsersByCustomer(Customer customer);
    
    // Update user details
    User updateUser(User user);
    
    // Update user email
    User updateUserEmail(Integer userId, String newEmail);
    
    // Update user password
    User updateUserPassword(Integer userId, String newPassword);
    
    // Update user role
    User updateUserRole(Integer userId, String newRole);
    
    // Get user sessions
    Set<Session> getUserSessions(Integer userId);
    
    // Get user audit logs
    Set<AuditLog> getUserAuditLogs(Integer userId);
    
    // Lock user account
    User lockUserAccount(Integer userId, Instant lockedUntil);
    
    // Unlock user account
    User unlockUserAccount(Integer userId);
    
    // Record failed login attempt
    User recordFailedLoginAttempt(Integer userId);
    
    // Reset failed login attempts
    User resetFailedLoginAttempts(Integer userId);
    
    // Check if user is locked
    boolean isUserLocked(Integer userId);
    
    // Get users with active sessions
    List<User> getUsersWithActiveSessions();
    
    // Get locked users
    List<User> getLockedUsers();
    
    // Get users with failed login attempts
    List<User> getUsersWithFailedLoginAttempts(Integer threshold);
    
    // Get recently created users
    List<User> getRecentlyCreatedUsers(Instant since);
    
    // Search users by email keyword
    List<User> searchUsersByEmailKeyword(String emailKeyword);
    
    // Count users by role
    long countUsersByRole(String role);
    
    // Check if email exists
    boolean isEmailTaken(String email);
    
    // Get users with audit logs
    List<User> getUsersWithAuditLogs();
    
    // Delete user
    void deleteUser(Integer userId);
}