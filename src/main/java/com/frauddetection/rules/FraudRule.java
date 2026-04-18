package com.frauddetection.rules;

import com.frauddetection.dto.RiskEvaluationResult;
import com.frauddetection.dto.TransactionRequest;

public interface FraudRule {

    /**
     * Evaluate this rule against the incoming transaction.
     * Implementations should call result.addRule(...) if the rule is triggered.
     *
     * @param request     the incoming transaction
     * @param result      the cumulative risk result to update
     * @param accountId   resolved account ID
     */
    void evaluate(TransactionRequest request, RiskEvaluationResult result, String accountId);

    /**
     * Human-readable name for this rule (used in audit logs).
     */
    String getRuleName();
}