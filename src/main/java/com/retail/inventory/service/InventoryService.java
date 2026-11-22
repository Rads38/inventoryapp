package com.retail.inventory.service;

import com.retail.inventory.domain.Inventory;
import com.retail.inventory.domain.InventoryReservation;
import com.retail.inventory.domain.ReservationStatus;
import com.retail.inventory.events.InventoryReservationRequestedEvent;
import com.retail.inventory.events.InventoryResultEvent;
import com.retail.inventory.repository.InventoryRepository;
import com.retail.inventory.repository.InventoryReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            InventoryReservationRepository reservationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public InventoryResultEvent reserveInventory(
            InventoryReservationRequestedEvent event
    ) {
        validate(event);

        // A redelivered Kafka event must not reserve stock a second time.
        var existing = reservationRepository.findByOrderId(event.orderId());
        if (existing.isPresent()) {
            InventoryReservation reservation = existing.get();
            return toResult(
                    reservation.getOrderId(),
                    reservation.getItem(),
                    reservation.getStatus(),
                    "Reservation request was already processed"
            );
        }

        var inventoryOptional = inventoryRepository.findByItemForUpdate(event.item());
        if (inventoryOptional.isEmpty()) {
            saveRejectedReservation(event);
            return toResult(
                    event.orderId(),
                    event.item(),
                    ReservationStatus.REJECTED,
                    "Item does not exist in inventory"
            );
        }

        Inventory inventory = inventoryOptional.get();
        if (inventory.getAvailableQuantity() < event.quantity()) {
            saveRejectedReservation(event);
            return toResult(
                    event.orderId(),
                    event.item(),
                    ReservationStatus.REJECTED,
                    "Insufficient stock"
            );
        }

        inventory.reserve(event.quantity());
        inventoryRepository.save(inventory);

        reservationRepository.save(new InventoryReservation(
                event.orderId(),
                event.item(),
                event.quantity(),
                ReservationStatus.RESERVED
        ));

        return toResult(
                event.orderId(),
                event.item(),
                ReservationStatus.RESERVED,
                "Inventory reserved successfully"
        );
    }

    private void validate(InventoryReservationRequestedEvent event) {
        if (event == null || event.orderId() == null || event.orderId().isBlank()) {
            throw new IllegalArgumentException("Order id is required");
        }
        if (event.item() == null || event.item().isBlank()) {
            throw new IllegalArgumentException("Item is required");
        }
        if (event.quantity() == null || event.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    private void saveRejectedReservation(InventoryReservationRequestedEvent event) {
        reservationRepository.save(new InventoryReservation(
                event.orderId(),
                event.item(),
                event.quantity(),
                ReservationStatus.REJECTED
        ));
    }

    private InventoryResultEvent toResult(String orderId,
                                          String item,
                                          ReservationStatus status,
                                          String message) {
        return new InventoryResultEvent(
                orderId,
                item,
                status.name(),
                message
        );
    }
}
