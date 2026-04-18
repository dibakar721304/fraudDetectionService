package com.frauddetection.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private String merchant;

    @Column(name = "merchant_category")
    private String merchantCategory;

    @Column(name = "location_country", length = 2)
    private String locationCountry;

    @Column(name = "location_city")
    private String locationCity;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "risk_score")
    private int riskScore;

    @Column(nullable = false)
    private boolean flagged;

    @Column(name = "flagged_reason")
    private String flaggedReason;

    @Column(nullable = false)
    private boolean reviewed;

    @Column(name = "review_outcome")
    @Enumerated(EnumType.STRING)
    private ReviewOutcome reviewOutcome;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.transactionTime == null) this.transactionTime = LocalDateTime.now();
        if (this.status == null) this.status = TransactionStatus.PENDING;
    }
}