# Inventory Service

Inventory microservice for the Retail Microservices Saga capstone.

## Responsibilities

- Consume reservation requests from Kafka topic `inventory-reservation-events`.
- Reserve available stock in a local database transaction.
- Make request processing idempotent using the unique order ID.
- Publish reservation results to Kafka topic `inventory-events`.

## Local setup

Requirements: Java 21, Maven, and Kafka running at `localhost:9092`.

```bash
mvn spring-boot:run
```

Service port: `7004`. Inventory API: `GET http://localhost:7004/inventory`.

Initial stock: Laptop (10), Mobile (20), Headphones (15).

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
