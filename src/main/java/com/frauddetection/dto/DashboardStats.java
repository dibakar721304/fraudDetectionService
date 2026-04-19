package com.frauddetection.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStats {
    private long totalTransactions;
    private long flaggedTransactions;
    private long pendingReviews;
    private long blockedTransactions;
}