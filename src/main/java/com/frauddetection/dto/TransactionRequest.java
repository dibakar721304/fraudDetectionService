package com.frauddetection.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransactionRequest {

    @NotBlank(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (ISO 4217)")
    private String currency;

    @NotBlank(message = "Merchant is required")
    private String merchant;

    private String merchantCategory;

    @NotBlank(message = "Location country is required")
    @Size(min = 2, max = 2, message = "Country must be 2-char ISO code")
    private String locationCountry;

    private String locationCity;

    private LocalDateTime transactionTime;
}