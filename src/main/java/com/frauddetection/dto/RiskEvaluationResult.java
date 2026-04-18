package com.frauddetection.dto;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RiskEvaluationResult {

    @Builder.Default
    private int totalRiskScore = 0;

    @Builder.Default
    private List<String> triggeredRules = new ArrayList<>();

    @Builder.Default
    private List<String> ruleDescriptions = new ArrayList<>();

    private boolean shouldBlock;
    private boolean shouldFlag;
    private String riskLevel;

    public void addRule(String ruleName, String description, int score) {
        this.triggeredRules.add(ruleName);
        this.ruleDescriptions.add(description);
        this.totalRiskScore += score;
    }

    public String getRiskLevel() {
        if (totalRiskScore >= 80) return "CRITICAL";
        if (totalRiskScore >= 50) return "HIGH";
        if (totalRiskScore >= 25) return "MEDIUM";
        return "LOW";
    }
}