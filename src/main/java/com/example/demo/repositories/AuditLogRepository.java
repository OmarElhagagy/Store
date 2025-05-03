package com.example.demo.repositories;

import com.example.demo.entities.AuditLog;
import com.example.demo.entities.Employee;
import com.example.demo.entities.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog>  findByUser(User user);
    List<AuditLog>  findByEmployee(Employee employee);
    List<AuditLog>  findByTableName(String tableName);
    List<AuditLog>  findByRecordId(Integer recordId);
    List<AuditLog>  findByTimestampBetween(Instant start, Instant end);
    List<AuditLog>  findByTableNameAndRecordId(String tableName, Integer recordId);
    Optional<AuditLog> findByUserAndEmployee(User user, Employee employee);
}
