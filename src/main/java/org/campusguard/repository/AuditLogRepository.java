package org.campusguard.repository;

import org.campusguard.entity.AuditLog;
import org.campusguard.entity.User;
import org.campusguard.entity.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserOrderByTimestampDesc(User user);

    List<AuditLog> findByActionOrderByTimestampDesc(AuditAction action);

    List<AuditLog> findAllByOrderByTimestampDesc();
}
