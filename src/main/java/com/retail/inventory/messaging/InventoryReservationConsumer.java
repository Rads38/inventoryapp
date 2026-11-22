package com.retail.inventory.messaging;

import com.retail.inventory.events.InventoryReservationRequestedEvent;
import com.retail.inventory.events.InventoryResultEvent;
import com.retail.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservationConsumer {

    private final InventoryService inventoryService;
    private final InventoryResultPublisher resultPublisher;

    public InventoryReservationConsumer(
            InventoryService inventoryService,
            InventoryResultPublisher resultPublisher
    ) {
        this.inventoryService = inventoryService;
        this.resultPublisher = resultPublisher;
    }

    @KafkaListener(
            topics = "inventory-reservation-events",
            groupId = "inventory-service-group"
    )
    public void consumeReservationRequest(
            InventoryReservationRequestedEvent event
    ) {
        System.out.println("Received inventory reservation request: " + event);

        InventoryResultEvent result =
                inventoryService.reserveInventory(event);

        resultPublisher.publishAndAwait(result);
    }
}
