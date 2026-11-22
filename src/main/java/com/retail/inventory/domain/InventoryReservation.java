package com.retail.inventory.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "inventory_reservations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reservation_order",
                columnNames = "order_id"
        )
)
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true, length = 100)
    private String orderId;

    @Column(nullable = false, length = 100)
    private String item;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected InventoryReservation() {
    }

    public InventoryReservation(String orderId, String item, Integer quantity,
                                ReservationStatus status) {
        this.orderId = orderId;
        this.item = item;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public String getOrderId() { return orderId; }
    public String getItem() { return item; }
    public Integer getQuantity() { return quantity; }
    public ReservationStatus getStatus() { return status; }
}
