package org.campusguard.audit;

import org.campusguard.entity.AuditLog;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.AuditAction;
import org.campusguard.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    public void logAction(User user, AuditAction action, String entityName, Long entityId, String metadata, String ipAddress) {
        AuditLog log = new AuditLog(user, action, entityName, entityId, metadata, ipAddress);
        auditLogRepository.save(log);
    }

    public void logActionSync(User user, AuditAction action, String entityName, Long entityId, String metadata, String ipAddress) {
        AuditLog log = new AuditLog(user, action, entityName, entityId, metadata, ipAddress);
        auditLogRepository.save(log);
    }
}
