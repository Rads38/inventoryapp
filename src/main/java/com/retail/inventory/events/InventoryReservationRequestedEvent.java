package com.retail.inventory.events;

public record InventoryReservationRequestedEvent(
        String orderId,
        String item,
        Integer quantity
) {
}
