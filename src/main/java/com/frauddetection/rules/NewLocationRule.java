package com.frauddetection.rules;

import com.frauddetection.config.FraudRuleConfig;
import com.frauddetection.dto.RiskEvaluationResult;
import com.frauddetection.dto.TransactionRequest;
import com.frauddetection.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewLocationRule implements FraudRule {

    private final FraudRuleConfig config;
    private final TransactionRepository transactionRepository;

    @Override
    public void evaluate(TransactionRequest request, RiskEvaluationResult result, String accountId) {
        List<String> knownCountries = transactionRepository.findKnownCountriesForAccount(accountId);

        if (knownCountries.isEmpty()) {
            // Brand new account — no history yet, lower risk signal
            result.addRule(
                getRuleName(),
                "No transaction history found for account - first transaction",
                10
            );
            return;
        }

        boolean isNewLocation = !knownCountries.contains(request.getLocationCountry());

        if (isNewLocation) {
            result.addRule(
                getRuleName(),
                String.format("Transaction from new country '%s'. Known countries: %s",
                    request.getLocationCountry(), String.join(", ", knownCountries)),
                config.getNewLocationRiskScore()
            );
            log.debug("New location detected: {} not in {}", request.getLocationCountry(), knownCountries);
        }
    }

    @Override
    public String getRuleName() {
        return "NEW_LOCATION";
    }
}