package com.frauddetection.dto;

import com.frauddetection.model.ReviewOutcome;
import com.frauddetection.model.TransactionStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionResponse {
    private String id;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String merchant;
    private String merchantCategory;
    private String locationCountry;
    private String locationCity;
    private LocalDateTime transactionTime;
    private TransactionStatus status;
    private int riskScore;
    private String riskLevel;
    private boolean flagged;
    private List<String> flaggedReasons;
    private boolean reviewed;
    private ReviewOutcome reviewOutcome;
    private LocalDateTime createdAt;
    private String message;
}