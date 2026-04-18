package com.frauddetection.scheduler;

import com.frauddetection.model.Transaction;
import com.frauddetection.model.TransactionStatus;
import com.frauddetection.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudReviewScheduler {

    private final TransactionRepository transactionRepository;

    /**
     * Every 60 seconds: check for transactions that have been pending review
     * for more than 10 minutes and log a warning (simulating SLA breach alerting).
     */
    @Scheduled(fixedDelay = 60_000)
    public void checkStalePendingReviews() {
        List<Transaction> pending = transactionRepository.findByFlaggedTrueAndReviewedFalse();
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);

        long staleCount = pending.stream()
            .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().isBefore(threshold))
            .count();

        if (staleCount > 0) {
            log.warn("[SCHEDULER] {} transaction(s) have been pending manual review for > 10 minutes. " +
                "Consider escalating to fraud team.", staleCount);
        } else {
            log.debug("[SCHEDULER] Pending review check: {} pending, 0 stale.", pending.size());
        }
    }

    /**
     * Every 2 minutes: log a summary of current fraud detection state.
     */
    @Scheduled(fixedDelay = 120_000)
    public void logFraudSummary() {
        long totalFlagged = transactionRepository.findByFlaggedTrue().size();
        long pendingReview = transactionRepository.findByFlaggedTrueAndReviewedFalse().size();
        long blocked = transactionRepository.findByStatus(TransactionStatus.BLOCKED).size();

        log.info("[SCHEDULER] Fraud Summary — Total flagged: {}, Pending review: {}, Blocked: {}",
            totalFlagged, pendingReview, blocked);
    }
}