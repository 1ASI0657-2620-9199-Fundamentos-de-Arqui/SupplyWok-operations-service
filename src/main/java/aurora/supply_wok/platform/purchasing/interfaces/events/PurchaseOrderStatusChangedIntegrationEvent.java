package aurora.supply_wok.platform.purchasing.interfaces.events;

/**
 * Published language event raised when a purchase order status changes.
 */
public record PurchaseOrderStatusChangedIntegrationEvent(
        Long purchaseOrderId,
        String code,
        Long supplierId,
        String supplierName,
        String restaurantName,
        String previousStatus,
        String currentStatus
) {
}
