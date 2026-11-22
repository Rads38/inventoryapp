package com.retail.inventory.events;

public record InventoryResultEvent(
        String orderId,
        String item,
        String reservationStatus,
        String message
) {
}
