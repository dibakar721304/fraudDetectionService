package com.frauddetection.rules;

import com.frauddetection.config.FraudRuleConfig;
import com.frauddetection.dto.RiskEvaluationResult;
import com.frauddetection.dto.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class UnusualHourRule implements FraudRule {

    private final FraudRuleConfig config;

    private static final int UNUSUAL_HOUR_START = 1;
    private static final int UNUSUAL_HOUR_END = 5;

    @Override
    public void evaluate(TransactionRequest request, RiskEvaluationResult result, String accountId) {
        LocalDateTime txTime = request.getTransactionTime() != null
            ? request.getTransactionTime()
            : LocalDateTime.now();

        int hour = txTime.getHour();

        if (hour >= UNUSUAL_HOUR_START && hour < UNUSUAL_HOUR_END) {
            result.addRule(
                getRuleName(),
                String.format("Transaction at unusual hour: %02d:00 (suspicious window: %02d:00-%02d:00)",
                    hour, UNUSUAL_HOUR_START, UNUSUAL_HOUR_END),
                config.getUnusualHourRiskScore()
            );
        }
    }

    @Override
    public String getRuleName() {
        return "UNUSUAL_HOUR";
    }
}