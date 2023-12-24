package com.example.wallet.service.extra;

import com.example.wallet.model.AuditLog;
import com.example.wallet.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
//public class AuditService {
//
//    private final AuditLogRepository auditLogRepository;
//
//    @Autowired
//    public AuditService(AuditLogRepository auditLogRepository) {
//        this.auditLogRepository = auditLogRepository;
//    }
//
//    public List<AuditLog> getAllAuditLogs() {
//        return auditLogRepository.findAll();
//    }
//
//    public AuditLog saveAuditLog(AuditLog auditLog) {
//        return auditLogRepository.save(auditLog);
//    }
//
//    // Additional methods as needed based on your requirements
//
//    // For example, you might want to implement a method to retrieve audit logs for a specific user
//    public List<AuditLog> getAuditLogsByUserId(Long userId) {
//        return auditLogRepository.findByUserId(userId);
//    }
//}
