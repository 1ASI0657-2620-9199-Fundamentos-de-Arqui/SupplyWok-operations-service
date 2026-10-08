package aurora.supply_wok.platform.inventory.interfaces.events;

/**
 * Published language event raised when an inventory item reaches its minimum stock.
 */
public record SupplyMinimumStockReachedIntegrationEvent(
        Long supplyId,
        String name,
        int currentStock,
        int minimumStockLevel,
        Long restaurantId
) {
    public SupplyMinimumStockReachedIntegrationEvent(Long supplyId, String name, int currentStock, int minimumStockLevel) {
        this(supplyId, name, currentStock, minimumStockLevel, null);
    }
}
