# Inventory Service

Inventory microservice for the Retail Microservices Saga capstone.

## Responsibilities

- Registers with Eureka as INVENTORY-SERVICE.
- Consumes reservation requests from inventory-reservation-events.
- Reserves stock in a local database transaction.
- Uses order ID for idempotent processing.
- Publishes results to inventory-events.

## Local setup

Run with Maven: mvn spring-boot:run

- Service name: INVENTORY-SERVICE
- Port: 7004
- Eureka Dashboard: http://localhost:8761
- Inventory API: http://localhost:7004/inventory
- H2 Console: http://localhost:7004/h2-console
- JDBC URL: jdbc:h2:mem:inventorydb
- H2 login: username sa; blank password

## Database and table state

Uses in-memory H2 database inventorydb. Initial stock: Laptop 10, Mobile 20, Headphones 15. Successful reservation reduces available stock and stores the reservation outcome. Repeated requests with the same order ID do not decrement stock twice. JPA ddl-auto=update.

## Kafka request payload

Topic: inventory-reservation-events

    {
      "orderId": "101",
      "item": "Laptop",
      "quantity": 1
    }

## Kafka result payload

Topic: inventory-events

    {
      "orderId": "101",
      "item": "Laptop",
      "reservationStatus": "RESERVED",
      "message": "Inventory reserved successfully"
    }

Eureka registry: http://localhost:8761/eureka/
