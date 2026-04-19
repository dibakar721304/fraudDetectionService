package com.frauddetection.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blacklisted_country")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BlacklistedCountry {

    @Id
    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column
    private String reason;
}