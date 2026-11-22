package com.retail.inventory.messaging;

import com.retail.inventory.events.InventoryReservationRequestedEvent;
import com.retail.inventory.events.InventoryResultEvent;
import com.retail.inventory.service.InventoryService;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
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

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            dltTopicSuffix = "-dlt",
            autoCreateTopics = "true",
            numPartitions = "1",
            replicationFactor = "1"
    )
    @KafkaListener(
            topics = "inventory-reservation-events",
            groupId = "inventory-service-group"
    )
    public void consumeReservationRequest(
            InventoryReservationRequestedEvent event
    ) {
        System.out.println("Received inventory reservation request: " + event);

        InventoryResultEvent result = inventoryService.reserveInventory(event);
        resultPublisher.publishAndAwait(result);
    }

    @DltHandler
    public void handleDlt(InventoryReservationRequestedEvent event) {
        System.err.println("INVENTORY REQUEST MOVED TO DLT");
        System.err.println("Failed inventory request: " + event);
    }
}
