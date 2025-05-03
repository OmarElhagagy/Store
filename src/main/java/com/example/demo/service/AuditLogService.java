package com.example.demo.service;

import com.example.demo.entities.AuditLog;
import com.example.demo.entities.User;
import com.example.demo.entities.Employee;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AuditLogService {
    AuditLog        createAuditLog(AuditLog auditLog);
    List<AuditLog>  findByUser(User user);
    List<AuditLog>  findByEmployee(Employee employee);
    List<AuditLog>  findByTableName(String tableName);
    List<AuditLog>  findByTimeRange(Instant start, Instant end);
    List<AuditLog>  findByTableNameAndRecordId(String tableName, Integer recordId);
    List<AuditLog>  findAll();
    Optional<AuditLog> findByUserAndEmployee(User user, Employee employee);
}
