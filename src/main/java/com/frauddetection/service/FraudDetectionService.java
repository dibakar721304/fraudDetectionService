package com.frauddetection.service;

import com.frauddetection.audit.AuditService;
import com.frauddetection.config.FraudRuleConfig;
import com.frauddetection.dto.*;
import com.frauddetection.model.*;
import com.frauddetection.repository.*;
import com.frauddetection.rules.RuleEngine;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final RuleEngine ruleEngine;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AuditService auditService;
    private final FraudRuleConfig config;

    @Transactional
    public TransactionResponse analyzeTransaction(TransactionRequest request) {
        // Validate account exists
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Account not found: " + request.getAccountId()));

        String transactionId = "txn-" + UUID.randomUUID().toString().substring(0, 8);

        auditService.logTransactionReceived(transactionId, account.getId(),
            String.format("Received transaction: amount=%.2f %s, merchant=%s, location=%s/%s",
                request.getAmount(), request.getCurrency(),
                request.getMerchant(), request.getLocationCountry(), request.getLocationCity()));

        // Run rule engine
        RiskEvaluationResult evaluation = ruleEngine.evaluate(request, account.getId());

        auditService.logRiskScoreCalculated(transactionId, account.getId(), evaluation);

        // Determine transaction status
        TransactionStatus status;
        String message;

        if (evaluation.isShouldBlock()) {
            status = TransactionStatus.BLOCKED;
            message = "Transaction BLOCKED due to high fraud risk score: " + evaluation.getTotalRiskScore();
            auditService.logTransactionBlocked(transactionId, account.getId(),
                evaluation.getTotalRiskScore(), evaluation.getTriggeredRules());
        } else if (evaluation.isShouldFlag()) {
            status = TransactionStatus.FLAGGED_FOR_REVIEW;
            message = "Transaction flagged for manual review. Risk score: " + evaluation.getTotalRiskScore();
            auditService.logTransactionFlagged(transactionId, account.getId(),
                evaluation.getTotalRiskScore(), evaluation.getTriggeredRules());
        } else {
            status = TransactionStatus.COMPLETED;
            message = "Transaction approved. Risk score: " + evaluation.getTotalRiskScore();
            auditService.logTransactionApproved(transactionId, account.getId());
        }

        boolean flagged = evaluation.isShouldBlock() || evaluation.isShouldFlag();
        String flaggedReason = flagged
            ? String.join(",", evaluation.getTriggeredRules())
            : null;

        // Persist transaction
        Transaction transaction = Transaction.builder()
            .id(transactionId)
            .accountId(account.getId())
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .merchant(request.getMerchant())
            .merchantCategory(request.getMerchantCategory())
            .locationCountry(request.getLocationCountry())
            .locationCity(request.getLocationCity())
            .transactionTime(request.getTransactionTime() != null
                ? request.getTransactionTime() : LocalDateTime.now())
            .status(status)
            .riskScore(evaluation.getTotalRiskScore())
            .flagged(flagged)
            .flaggedReason(flaggedReason)
            .reviewed(false)
            .build();

        transactionRepository.save(transaction);
        log.info("Transaction {} saved with status={}, riskScore={}", transactionId, status, evaluation.getTotalRiskScore());

        return buildResponse(transaction, evaluation, message);
    }

    @Transactional
    public TransactionResponse submitManualReview(String transactionId, ReviewRequest reviewRequest) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));

        if (!transaction.isFlagged()) {
            throw new IllegalStateException("Transaction " + transactionId + " is not flagged for review");
        }

        transaction.setReviewed(true);
        transaction.setReviewOutcome(reviewRequest.getOutcome());

        // Update status based on review outcome
        switch (reviewRequest.getOutcome()) {
            case LEGITIMATE, FALSE_POSITIVE -> transaction.setStatus(TransactionStatus.APPROVED_AFTER_REVIEW);
            case CONFIRMED_FRAUD -> transaction.setStatus(TransactionStatus.BLOCKED);
        }

        transactionRepository.save(transaction);

        auditService.logManualReview(
            transactionId,
            transaction.getAccountId(),
            reviewRequest.getOutcome().name(),
            reviewRequest.getReviewedBy(),
            reviewRequest.getReviewerNotes()
        );

        log.info("Manual review completed for txn={}: outcome={}", transactionId, reviewRequest.getOutcome());

        return buildResponse(transaction, null, "Review submitted: " + reviewRequest.getOutcome());
    }

    public List<TransactionResponse> getPendingReviews() {
        return transactionRepository.findPendingReviewOrderByRiskDesc()
            .stream()
            .map(t -> buildResponse(t, null, "Pending review"))
            .toList();
    }

    public List<TransactionResponse> getAccountTransactions(String accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionTimeDesc(accountId)
            .stream()
            .map(t -> buildResponse(t, null, null))
            .toList();
    }

    public TransactionResponse getTransaction(String transactionId) {
        Transaction t = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));
        return buildResponse(t, null, null);
    }

    public List<TransactionResponse> getAllFlagged() {
        return transactionRepository.findByFlaggedTrue()
            .stream()
            .map(t -> buildResponse(t, null, null))
            .toList();
    }

    // --- Dashboard stats ---
    public DashboardStats getDashboardStats() {
        long total = transactionRepository.count();
        long flagged = transactionRepository.findByFlaggedTrue().size();
        long pendingReview = transactionRepository.findByFlaggedTrueAndReviewedFalse().size();
        long blocked = transactionRepository.findByStatus(TransactionStatus.BLOCKED).size();

        return DashboardStats.builder()
            .totalTransactions(total)
            .flaggedTransactions(flagged)
            .pendingReviews(pendingReview)
            .blockedTransactions(blocked)
            .build();
    }

    // --- Private helpers ---
    private TransactionResponse buildResponse(Transaction t, RiskEvaluationResult eval, String message) {
        List<String> reasons = (t.getFlaggedReason() != null && !t.getFlaggedReason().isBlank())
            ? Arrays.asList(t.getFlaggedReason().split(","))
            : Collections.emptyList();

        return TransactionResponse.builder()
            .id(t.getId())
            .accountId(t.getAccountId())
            .amount(t.getAmount())
            .currency(t.getCurrency())
            .merchant(t.getMerchant())
            .merchantCategory(t.getMerchantCategory())
            .locationCountry(t.getLocationCountry())
            .locationCity(t.getLocationCity())
            .transactionTime(t.getTransactionTime())
            .status(t.getStatus())
            .riskScore(t.getRiskScore())
            .riskLevel(eval != null ? eval.getRiskLevel() : riskLevelFromScore(t.getRiskScore()))
            .flagged(t.isFlagged())
            .flaggedReasons(reasons)
            .reviewed(t.isReviewed())
            .reviewOutcome(t.getReviewOutcome())
            .createdAt(t.getCreatedAt())
            .message(message)
            .build();
    }

    private String riskLevelFromScore(int score) {
        if (score >= 80) return "CRITICAL";
        if (score >= 50) return "HIGH";
        if (score >= 25) return "MEDIUM";
        return "LOW";
    }
}