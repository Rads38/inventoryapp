package com.retail.inventory.messaging;

import com.retail.inventory.events.InventoryResultEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class InventoryResultPublisher {

    private static final String TOPIC = "inventory-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryResultPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishAndAwait(InventoryResultEvent event) {
        try {
            kafkaTemplate.send(TOPIC, event.orderId(), event)
                    .get(10, TimeUnit.SECONDS);
            System.out.println("Published inventory result: " + event);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Interrupted while publishing inventory result",
                    ex
            );
        } catch (ExecutionException | TimeoutException ex) {
            throw new IllegalStateException(
                    "Could not publish inventory result for order " + event.orderId(),
                    ex
            );
        }
    }
}
