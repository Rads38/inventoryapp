package com.retail.inventory.domain;

import jakarta.persistence.*;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inventory_item",
                columnNames = "item"
        )
)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String item;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    protected Inventory() {
    }

    public Inventory(String item, Integer availableQuantity) {
        this.item = item;
        this.availableQuantity = availableQuantity;
    }

    public Long getId() {
        return id;
    }

    public String getItem() {
        return item;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void reserve(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Reservation quantity must be positive");
        }
        if (availableQuantity < quantity) {
            throw new IllegalStateException("Insufficient inventory for item: " + item);
        }
        availableQuantity -= quantity;
    }
}
