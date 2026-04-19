package com.frauddetection.rules;

import com.frauddetection.config.FraudRuleConfig;
import com.frauddetection.dto.RiskEvaluationResult;
import com.frauddetection.dto.TransactionRequest;
import com.frauddetection.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class LargeTransferRule implements FraudRule {

    private final FraudRuleConfig config;
    private final TransactionRepository transactionRepository;

    @Override
    public void evaluate(TransactionRequest request, RiskEvaluationResult result, String accountId) {
        BigDecimal threshold = config.getLargeTransferThreshold();

        if (request.getAmount().compareTo(threshold) > 0) {
            // Check if this is unusually large compared to account history
            BigDecimal avgAmount = transactionRepository.findAverageAmountForAccount(accountId);

            int score = config.getLargeAmountRiskScore();

            // Extra risk if it's 5x the historical average
            if (avgAmount != null && avgAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = request.getAmount().divide(avgAmount, 2, java.math.RoundingMode.HALF_UP);
                if (ratio.compareTo(new BigDecimal("5")) > 0) {
                    score += 15; // Extra 15 points for extreme outlier
                    log.debug("Transaction amount is {}x the account average - elevated risk", ratio);
                }
            }

            result.addRule(
                getRuleName(),
                String.format("Transaction amount %.2f %s exceeds threshold %.2f (account avg: %.2f)",
                    request.getAmount(), request.getCurrency(), threshold,
                    avgAmount != null ? avgAmount : BigDecimal.ZERO),
                score
            );
        }
    }

    @Override
    public String getRuleName() {
        return "LARGE_AMOUNT";
    }
}