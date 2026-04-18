package com.frauddetection.repository;

import com.frauddetection.model.AuditLog;
import com.frauddetection.model.AuditEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTransactionIdOrderByEventTimeDesc(String transactionId);
    List<AuditLog> findByAccountIdOrderByEventTimeDesc(String accountId);
    List<AuditLog> findByEventTypeOrderByEventTimeDesc(AuditEventType eventType);
}