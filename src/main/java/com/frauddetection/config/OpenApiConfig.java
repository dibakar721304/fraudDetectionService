package com.frauddetection.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fraudDetectionOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Fraud Detection API")
                .description("""
                    Real-time fraud detection system built with Java 21, Spring Boot 3, and H2.
                    
                    Features:
                    - Rule-based fraud detection engine (large amount, new location, velocity, unusual hour, blacklisted country/account)
                    - Risk scoring system (0–100) with CRITICAL / HIGH / MEDIUM / LOW levels
                    - Auto-blocking (score ≥ 80) and flagging for review (score ≥ 50)
                    - Manual review workflow with LEGITIMATE / CONFIRMED_FRAUD / FALSE_POSITIVE outcomes
                    - Full async audit log trail per transaction and account
                    - Historical + real-time data combined for smarter decisions
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Fraud Detection Team")
                    .email("fraud@example.com")));
    }
}