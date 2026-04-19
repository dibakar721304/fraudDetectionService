package com.frauddetection.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(nullable = false, length = 2)
    private String country;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_blacklisted", nullable = false)
    private boolean isBlacklisted;
}