# Fraud Detection Service

A production-grade Spring Boot REST API for detecting potentially fraudulent financial transactions using configurable machine learning rules and heuristic-based analysis.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Usage Examples](#usage-examples)
- [Monitoring & Observability](#monitoring--observability)
- [Development](#development)
- [License](#license)

## Overview

This service provides real-time fraud detection for financial transactions using configurable rules including transaction amount, velocity analysis, geographic location, and behavioral patterns. It is designed for high-throughput, low-latency transaction processing.

## Features

- ✅ Real-time transaction fraud detection
- ✅ Configurable rule-based detection engine
- ✅ Risk scoring (0-100 scale)
- ✅ Multiple decision outcomes (BLOCK, REVIEW, APPROVE)
- ✅ RESTful API with OpenAPI/Swagger documentation
- ✅ Health checks and observability
- ✅ Built-in metrics and monitoring
- ✅ In-memory H2 database for development

## Prerequisites

| Requirement | Version |
|-----------|---------|
| Java | 21+     |
| Maven | 3.6+    |
| Spring Boot | 3.0+    |


## Installation

### Clone the Repository

```bash
git clone https://github.com/dibakar721304/fraudDetectionService.git
cd fraudDetectionService
```

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn clean spring-boot:run
```

The service will start on **http://localhost:8080**

### Docker (Optional)

```bash
docker build -t fraud-detection-service .
docker run -p 8080:8080 fraud-detection-service
```

## Configuration

Fraud detection rules are configured in `src/main/resources/application.yml`:

```yaml
fraud:
  detection:
    largeTransferThreshold: 10000.00      # Amount threshold to flag large transfers
    velocityMaxTransactions: 5             # Max transactions allowed in time window
    velocityWindowMinutes: 10              # Time window for velocity checks
    blockThreshold: 80                     # Risk score threshold for blocking
    reviewThreshold: 50                    # Risk score threshold for manual review
```

**Environment Variables** (overrides YAML):

```bash
FRAUD_DETECTION_LARGE_TRANSFER_THRESHOLD=10000
FRAUD_DETECTION_VELOCITY_MAX_TRANSACTIONS=5
FRAUD_DETECTION_VELOCITY_WINDOW_MINUTES=10
FRAUD_DETECTION_BLOCK_THRESHOLD=80
FRAUD_DETECTION_REVIEW_THRESHOLD=50
```

## API Documentation

### OpenAPI/Swagger UI

Interactive API documentation is available at:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Usage Examples

### Check Transaction for Fraud

**Request:**

```bash
curl -X POST http://localhost:8080/api/fraud/check \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "12345",
    "amount": 12000.00,
    "country": "US",
    "timestamp": "2024-06-01T10:15:30Z"
  }'
```

**Response:**

```json
{
  "fraudulent": true,
  "riskScore": 85,
  "decision": "BLOCK",
  "reasons": [
    "LARGE_AMOUNT",
    "HIGH_VELOCITY"
  ]
}
```

**Response Codes:**

| Code | Description |
|------|-------------|
| 200  | Success |
| 400  | Invalid request parameters |
| 500  | Internal server error |

### Get Fraud Detection Rules

**Request:**

```bash
curl -X GET http://localhost:8080/api/fraud/rules \
  -H "Content-Type: application/json"
```

**Response:**

```json
{
  "largeTransferThreshold": 10000.00,
  "velocityMaxTransactions": 5,
  "velocityWindowMinutes": 10,
  "blockThreshold": 80,
  "reviewThreshold": 50
}
```

## Monitoring & Observability

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

Response indicates service UP/DOWN status.

### Application Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

Returns available metrics (response times, transaction counts, etc.)

### Application Info

```bash
curl http://localhost:8080/actuator/info
```

Returns build and version information.

### H2 Database Console (Development Only)

Accessible at: **http://localhost:8080/h2-console**

- **JDBC URL**: `jdbc:h2:mem:frauddb`
- **Username**: `sa`
- **Password**: (leave blank)

⚠️ **Note**: Do not expose H2 console in production.

## Development

### Project Structure

```
fraudDetectionService/
├── src/main/java/
│   └── com/frauddetection/
│       ├── controller/          # REST endpoints
│       ├── service/             # Business logic
│       ├── model/               # Data models
│       └── config/              # Configuration classes
├── src/main/resources/
│   └── application.yml          # Configuration
├── src/test/
│   └── java/                    # Unit and integration tests
└── pom.xml
```

### Running Tests

```bash
mvn test
```

### Code Quality

```bash
mvn clean verify
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Port 8080 already in use | Change port: `server.port=8081` in application.yml |
| Build fails | Clear Maven cache: `mvn clean install` |
| Database connection error | Check H2 JDBC URL and credentials |

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request


## Support

For issues, questions, or suggestions, please create an issue in the [GitHub repository](https://github.com/dibakar721304/fraudDetectionService/issues).

---

**Last Updated**: 2026-04-19

