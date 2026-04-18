package com.frauddetection.controller;

import com.frauddetection.audit.AuditService;
import com.frauddetection.dto.*;
import com.frauddetection.model.AuditLog;
import com.frauddetection.service.FraudDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fraud Detection API", description = "Real-time transaction fraud detection and review")
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;
    private final AuditService auditService;

    @PostMapping("/analyze")
    @Operation(summary = "Analyze a transaction for fraud in real-time",
               description = "Runs all fraud rules, calculates a risk score, and returns APPROVED, FLAGGED, or BLOCKED status.")
    public ResponseEntity<TransactionResponse> analyzeTransaction(
            @Valid @RequestBody TransactionRequest request) {
        log.info("Received transaction analysis request for account={}", request.getAccountId());
        TransactionResponse response = fraudDetectionService.analyzeTransaction(request);
        HttpStatus status = switch (response.getStatus()) {
            case BLOCKED -> HttpStatus.FORBIDDEN;
            case FLAGGED_FOR_REVIEW -> HttpStatus.ACCEPTED;
            default -> HttpStatus.OK;
        };
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/transactions/{transactionId}")
    @Operation(summary = "Get a single transaction by ID")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String transactionId) {
        return ResponseEntity.ok(fraudDetectionService.getTransaction(transactionId));
    }

    @GetMapping("/accounts/{accountId}/transactions")
    @Operation(summary = "Get full transaction history for an account")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(
            @PathVariable String accountId) {
        return ResponseEntity.ok(fraudDetectionService.getAccountTransactions(accountId));
    }

    @GetMapping("/flagged")
    @Operation(summary = "Get all flagged transactions (reviewed and unreviewed)")
    public ResponseEntity<List<TransactionResponse>> getAllFlagged() {
        return ResponseEntity.ok(fraudDetectionService.getAllFlagged());
    }

    @GetMapping("/review/pending")
    @Operation(summary = "Get all transactions pending manual review, ordered by risk score descending")
    public ResponseEntity<List<TransactionResponse>> getPendingReviews() {
        return ResponseEntity.ok(fraudDetectionService.getPendingReviews());
    }

    @PostMapping("/review/{transactionId}")
    @Operation(summary = "Submit manual review outcome for a flagged transaction",
               description = "Outcomes: LEGITIMATE, CONFIRMED_FRAUD, FALSE_POSITIVE")
    public ResponseEntity<TransactionResponse> submitReview(
            @PathVariable String transactionId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        log.info("Manual review submitted for txn={}, outcome={}", transactionId, reviewRequest.getOutcome());
        return ResponseEntity.ok(fraudDetectionService.submitManualReview(transactionId, reviewRequest));
    }

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get high-level fraud detection dashboard statistics")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(fraudDetectionService.getDashboardStats());
    }

    @GetMapping("/audit/transaction/{transactionId}")
    @Operation(summary = "Get full audit trail for a specific transaction")
    public ResponseEntity<List<AuditLog>> getTransactionAuditLogs(@PathVariable String transactionId) {
        return ResponseEntity.ok(auditService.getLogsForTransaction(transactionId));
    }

    @GetMapping("/audit/account/{accountId}")
    @Operation(summary = "Get full audit trail for an account (all transactions)")
    public ResponseEntity<List<AuditLog>> getAccountAuditLogs(@PathVariable String accountId) {
        return ResponseEntity.ok(auditService.getLogsForAccount(accountId));
    }
}