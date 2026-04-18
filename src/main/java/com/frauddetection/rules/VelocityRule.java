package com.frauddetection.rules;

import com.frauddetection.config.FraudRuleConfig;
import com.frauddetection.dto.RiskEvaluationResult;
import com.frauddetection.dto.TransactionRequest;
import com.frauddetection.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class VelocityRule implements FraudRule {

    private final FraudRuleConfig config;
    private final TransactionRepository transactionRepository;

    @Override
    public void evaluate(TransactionRequest request, RiskEvaluationResult result, String accountId) {
        LocalDateTime windowStart = LocalDateTime.now()
            .minusMinutes(config.getVelocityWindowMinutes());

        long recentCount = transactionRepository.countRecentTransactions(accountId, windowStart);

        if (recentCount >= config.getVelocityMaxTransactions()) {
            int score = config.getHighVelocityRiskScore();

            // Escalate score if significantly over threshold
            if (recentCount >= config.getVelocityMaxTransactions() * 2) {
                score += 20;
            }

            result.addRule(
                getRuleName(),
                String.format("%d transactions in the last %d minutes (threshold: %d)",
                    recentCount, config.getVelocityWindowMinutes(), config.getVelocityMaxTransactions()),
                score
            );
            log.debug("Velocity breach: {} txns in {}min window for account {}",
                recentCount, config.getVelocityWindowMinutes(), accountId);
        }
    }

    @Override
    public String getRuleName() {
        return "HIGH_VELOCITY";
    }
}