**Fraud Detection Service**
A Spring Boot REST API for detecting potentially fraudulent financial transactions using configurable rules (amount, velocity, location, etc.).

**Prerequisites**
Java 21
Maven 3.6+

**Running the Application**
mvn clean spring-boot:run
The service will start on port 8080.
**API Endpoints**

Method         Endpoint                 Description

POST        /api/fraud/check          Check if a transaction is fraudulent
GET         /api/fraud/rules          Get current fraud detection rule thresholds
GET         /actuator/health          Application health status
GET         /actuator/info            Application info
GET         /actuator/metrics         Application metrics

**Usage**
Check Transaction

request:
POST /api/fraud/check
Content-Type: application/json

{
"accountId": "12345",
"amount": 12000.00,
"country": "US",
"timestamp": "2024-06-01T10:15:30Z"
}
response:
{
"fraudulent": true,
"riskScore": 85,
"decision": "BLOCK",
"reasons": ["LARGE_AMOUNT", "HIGH_VELOCITY"]
}

Get Fraud Rules

{
"largeTransferThreshold": 10000.00,
"velocityMaxTransactions": 5,
"velocityWindowMinutes": 10,
"blockThreshold": 80,
"reviewThreshold": 50
}

**API Documentation**
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI Docs: http://localhost:8080/api-docs
**Actuator Endpoints**
Health: http://localhost:8080/actuator/health
Info: http://localhost:8080/actuator/info
Metrics: http://localhost:8080/actuator/metrics
**H2 Database Console**
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:frauddb
Username: sa
Password: (leave blank)

Configuration for fraud rules can be found in src/main/resources/application.yml.