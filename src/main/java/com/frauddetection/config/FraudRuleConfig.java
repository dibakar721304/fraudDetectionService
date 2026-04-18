package com.frauddetection.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "fraud.rules")
@Getter @Setter
public class FraudRuleConfig {
    private BigDecimal largeTransferThreshold = new BigDecimal("10000.00");
    private int velocityMaxTransactions = 5;
    private int velocityWindowMinutes = 10;
    private int newLocationRiskScore = 40;
    private int largeAmountRiskScore = 30;
    private int highVelocityRiskScore = 35;
    private int unusualHourRiskScore = 20;
    private int blacklistedCountryRiskScore = 60;
    private int blockThreshold = 80;
    private int reviewThreshold = 50;
}