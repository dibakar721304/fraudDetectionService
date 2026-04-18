package com.frauddetection.audit;

import com.frauddetection.dto.RiskEvaluationResult;
import com.frauddetection.model.AuditEventType;
import com.frauddetection.model.AuditLog;
import com.frauddetection.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void logTransactionReceived(String transactionId, String accountId, String details) {
        save(AuditLog.builder()
            .transactionId(transactionId)
            .accountId(accountId)
            .eventType(AuditEventType.TRANSACTION_RECEIVED)
            .description(details)
            .performedBy("SYSTEM")
            .build());
    }

    @Async
    public void logRiskScoreCalculated(String transactionId, String accountId, RiskEvaluationResult result) {
        save(AuditLog.builder()
            .transactionId(transactionId)
            .accountId(accountId)
            .eventType(AuditEventType.RISK_SCORE_CALCULATED)
            .description(String.format("Risk score: %d (%s). Rules triggered: %s",
                result.getTotalRiskScore(), result.getRiskLevel(),
                String.join(", ", result.getTriggeredRules())))
            .riskScoreSnapshot(result.getTotalRiskScore())
            .triggeredRules(String.join(",", result.getTriggeredRules()))
            .performedBy("RULE_ENGINE")
            .build());
    }

    @Async
    public void logTransactionFlagged(String transactionId, String accountId, int riskScore, List<String> reasons) {
        save(AuditLog.builder()
            .transactionId(transactionId)
            .accountId(accountId)
            .eventType(AuditEventType.TRANSACTION_FLAGGED)
            .description(String.format("Transaction flagged for review. Score: %d. Reasons: %s",
                riskScore, String.join(", ", reasons)))
            .riskScoreSnapshot(riskScore)
            .triggeredRules(String.join(",", reasons))
            .performedBy("SYSTEM")
            .build());
    }

    @Async
    public void logTransactionBlocked(String transactionId, String accountId, int riskScore, List<String> reasons) {
        save(AuditLog.builder()
            .transactionId(transactionId)
            .accountId(accountId)
            .eventType(AuditEventType.TRANSACTION_BLOCKED)
            .description(String.format("Transaction BLOCKED. Score: %d. Reasons: %s",
                riskScore, String.join(", ", reasons)))
            .riskScoreSnapshot(riskScore)
            .triggeredRules(String.join(",", reasons))
            .performedBy("SYSTEM")
            .build());
    }

    @Async
    public void logTransactionApproved(String transactionId, String accountId) {
        save(AuditLog.builder()
            .transactionId(transactionId)
            .accountId(accountId)
            .eventType(AuditEventType.TRANSACTION_APPROVED)
            .description("Transaction passed all fraud checks and was approved")
            .performedBy("SYSTEM")
            .build());
    }

    @Async
    public void logManualReview(String transactionId, String accountId, String outcome,
                                String reviewer, String notes) {
        save(AuditLog.builder()
            .transactionId(transactionId)
            .accountId(accountId)
            .eventType(AuditEventType.MANUAL_REVIEW_COMPLETED)
            .description(String.format("Manual review completed. Outcome: %s. Reviewer: %s. Notes: %s",
                outcome, reviewer, notes))
            .performedBy(reviewer != null ? reviewer : "UNKNOWN")
            .build());
    }

    public List<AuditLog> getLogsForTransaction(String transactionId) {
        return auditLogRepository.findByTransactionIdOrderByEventTimeDesc(transactionId);
    }

    public List<AuditLog> getLogsForAccount(String accountId) {
        return auditLogRepository.findByAccountIdOrderByEventTimeDesc(accountId);
    }

    private void save(AuditLog log) {
        try {
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Audit failure must never crash the main flow
            this.log.error("Failed to save audit log: {}", e.getMessage());
        }
    }
}