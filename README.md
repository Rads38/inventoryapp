# Inventory Service

Inventory microservice for the Retail Microservices Saga capstone.

## Technology

- Java 21
- Spring Boot 3
- Spring Data JPA
- Apache Kafka
- H2 for local development

## Branching

- `main`: stable baseline
- `feature/saga-inventory`: Saga inventory reservation implementation

The service consumes inventory reservation requests, reserves stock transactionally, and publishes inventory results.
