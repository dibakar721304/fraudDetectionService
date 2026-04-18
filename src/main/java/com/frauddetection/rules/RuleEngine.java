package com.frauddetection.rules;

import com.frauddetection.config.FraudRuleConfig;
import com.frauddetection.dto.RiskEvaluationResult;
import com.frauddetection.dto.TransactionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleEngine {

    private final List<FraudRule> rules;
    private final FraudRuleConfig config;

    /**
     * Run all registered fraud rules against a transaction.
     * Rules are Spring beans and auto-discovered via List<FraudRule> injection.
     */
    public RiskEvaluationResult evaluate(TransactionRequest request, String accountId) {
        RiskEvaluationResult result = RiskEvaluationResult.builder().build();

        log.info("Evaluating {} rules for account={}, amount={} {}",
            rules.size(), accountId, request.getAmount(), request.getCurrency());

        for (FraudRule rule : rules) {
            try {
                rule.evaluate(request, result, accountId);
            } catch (Exception ex) {
                log.error("Rule {} failed with exception: {}", rule.getRuleName(), ex.getMessage(), ex);
                // Rule failure should not block the pipeline — log and continue
            }
        }

        // Cap score at 100
        int cappedScore = Math.min(result.getTotalRiskScore(), 100);
        result.setTotalRiskScore(cappedScore);
        result.setShouldBlock(cappedScore >= config.getBlockThreshold());
        result.setShouldFlag(cappedScore >= config.getReviewThreshold());

        log.info("Rule evaluation complete: score={}, level={}, block={}, flag={}, rules={}",
            cappedScore, result.getRiskLevel(), result.isShouldBlock(),
            result.isShouldFlag(), result.getTriggeredRules());

        return result;
    }
}