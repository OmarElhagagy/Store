package com.example.demo.repositories;

import com.example.demo.entities.Customer;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    // Find by email
    Optional<User> findByEmail(String email);
    
    // Find by customer
    List<User> findByCustomer(Customer customer);
    
    // Find by role
    List<User> findByRole(String role);
    
    // Find users with active sessions
    @Query("SELECT DISTINCT u FROM User u JOIN u.sessions s WHERE s.expiresAt > :now")
    List<User> findUsersWithActiveSessions(Instant now);
    
    // Find locked users
    List<User> findByLockedUntilGreaterThan(Instant now);
    
    // Find users with failed login attempts
    List<User> findByFailedLoginsGreaterThan(Integer threshold);
    
    // Find users created after date
    List<User> findByCreatedAtAfter(Instant date);
    
    // Find users created before date
    List<User> findByCreatedAtBefore(Instant date);
    
    // Find by email containing
    List<User> findByEmailContaining(String emailKeyword);
    
    // Count users by role
    long countByRole(String role);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find users with audit logs
    @Query("SELECT DISTINCT u FROM User u WHERE SIZE(u.auditLogs) > 0")
    List<User> findUsersWithAuditLogs();
}