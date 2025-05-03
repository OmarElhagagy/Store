package com.example.demo.service.impl;

import com.example.demo.entities.AuditLog;
import com.example.demo.repositories.AuditLogRepository;
import com.example.demo.service.AuditLogService;
import com.example.demo.entities.Employee;
import com.example.demo.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLog createAuditLog(AuditLog auditLog) {
        // Could add validation or preprocessing before saving maube later idk
        if (auditLog == null) {
            throw new IllegalArgumentException("AuditLog cannot be null");
        }
        if (auditLog.getTableName() == null || auditLog.getTableName().isEmpty()) {
            throw new IllegalArgumentException("Table name is required");
        }
        auditLog.setTimestamp(Instant.now());
        return auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLog> findByUser(User user) {
        return auditLogRepository.findByUser(user);
    }

    @Override
    public List<AuditLog> findByEmployee(Employee employee) {
        return auditLogRepository.findByEmployee(employee);
    }

    @Override
    public List<AuditLog> findByTableName(String tableName) {
        return auditLogRepository.findByTableName(tableName);
    }

    @Override
    public List<AuditLog> findByTimeRange(Instant start, Instant end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }

    @Override
    public List<AuditLog> findByTableNameAndRecordId(String tableName, Integer recordId) {
        return auditLogRepository.findByTableNameAndRecordId(tableName, recordId);
    }

    @Override
    public List<AuditLog> findAll() {
        return auditLogRepository.findAll();
    }

    @Override
    public Optional<AuditLog> findByUserAndEmployee(User user, Employee employee) {
        return auditLogRepository.findByUserAndEmployee(user, employee);
    }
}
