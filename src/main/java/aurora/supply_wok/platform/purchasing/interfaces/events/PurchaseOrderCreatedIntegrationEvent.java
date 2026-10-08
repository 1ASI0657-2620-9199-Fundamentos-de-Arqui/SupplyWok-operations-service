package aurora.supply_wok.platform.purchasing.interfaces.events;

/**
 * Published language event raised when a purchase order is created.
 */
public record PurchaseOrderCreatedIntegrationEvent(
        Long purchaseOrderId,
        String code,
        Long supplierId,
        String supplierName,
        String restaurantName,
        String priority,
        String status,
        Long restaurantId,
        String supplierEmail
) {
    public PurchaseOrderCreatedIntegrationEvent(
            Long purchaseOrderId,
            String code,
            Long supplierId,
            String supplierName,
            String restaurantName,
            String priority,
            String status
    ) {
        this(purchaseOrderId, code, supplierId, supplierName, restaurantName, priority, status, null, null);
    }
}
