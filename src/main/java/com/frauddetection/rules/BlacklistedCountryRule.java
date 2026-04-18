package com.frauddetection.rules;

import com.frauddetection.config.FraudRuleConfig;
import com.frauddetection.dto.RiskEvaluationResult;
import com.frauddetection.dto.TransactionRequest;
import com.frauddetection.repository.BlacklistedCountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BlacklistedCountryRule implements FraudRule {

    private final FraudRuleConfig config;
    private final BlacklistedCountryRepository blacklistedCountryRepository;

    @Override
    public void evaluate(TransactionRequest request, RiskEvaluationResult result, String accountId) {
        String country = request.getLocationCountry();

        if (isBlacklisted(country)) {
            result.addRule(
                getRuleName(),
                String.format("Transaction originates from blacklisted country: %s", country),
                config.getBlacklistedCountryRiskScore()
            );
            log.warn("Transaction from blacklisted country {} for account {}", country, accountId);
        }
    }

    @Cacheable("blacklistedCountries")
    public boolean isBlacklisted(String countryCode) {
        return blacklistedCountryRepository.existsByCountryCode(countryCode);
    }

    @Override
    public String getRuleName() {
        return "BLACKLISTED_COUNTRY";
    }
}