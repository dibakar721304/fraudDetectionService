package com.frauddetection.dto;

import com.frauddetection.model.ReviewOutcome;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewRequest {

    @NotNull(message = "Review outcome is required")
    private ReviewOutcome outcome;

    private String reviewerNotes;

    private String reviewedBy;
}