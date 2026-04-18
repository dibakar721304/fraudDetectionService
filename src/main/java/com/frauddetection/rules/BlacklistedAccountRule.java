package com.frauddetection.rules;

import com.frauddetection.dto.RiskEvaluationResult;
import com.frauddetection.dto.TransactionRequest;
import com.frauddetection.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlacklistedAccountRule implements FraudRule {

    private final AccountRepository accountRepository;

    @Override
    public void evaluate(TransactionRequest request, RiskEvaluationResult result, String accountId) {
        accountRepository.findById(accountId).ifPresent(account -> {
            if (account.isBlacklisted()) {
                result.addRule(
                    getRuleName(),
                    String.format("Account %s is on the blacklist", accountId),
                    100 // Immediate block
                );
                log.warn("Blacklisted account {} attempted transaction", accountId);
            }
        });
    }

    @Override
    public String getRuleName() {
        return "BLACKLISTED_ACCOUNT";
    }
}