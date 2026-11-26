# Inventory Service

Inventory microservice for the Retail Microservices Saga capstone.

## Responsibilities

- Registers with Eureka as `INVENTORY-SERVICE`.
- Consumes reservation requests from Kafka topic `inventory-reservation-events`.
- Reserves available stock in a local database transaction.
- Makes request processing idempotent using the unique order ID.
- Publishes reservation results to Kafka topic `inventory-events`.

## Local setup

Requirements: Java 21, Maven, Eureka Server running at `localhost:8761`, and Kafka running at `localhost:9092`.

```bash
mvn spring-boot:run
```

Service port: `7004`. Inventory API: `GET http://localhost:7004/inventory`.

H2 console: `http://localhost:7004/h2-console`  
JDBC URL: `jdbc:h2:mem:inventorydb`  
Username: `sa`; password: blank.

Initial stock: Laptop (10), Mobile (20), Headphones (15).

## Kafka event contracts

Request message on `inventory-reservation-events`:

```json
{
  "orderId": "101",
  "item": "Laptop",
  "quantity": 1
}
```

Result message on `inventory-events`:

```json
{
  "orderId": "101",
  "item": "Laptop",
  "reservationStatus": "RESERVED",
  "message": "Inventory reserved successfully"
}
```

Repeated requests with the same order ID return the stored outcome without decrementing stock again.
