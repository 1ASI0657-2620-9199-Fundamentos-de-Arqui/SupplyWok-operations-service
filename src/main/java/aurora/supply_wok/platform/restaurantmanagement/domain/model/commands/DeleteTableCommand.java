package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

public record DeleteTableCommand(Long restaurantId, Long tableId) {
    public DeleteTableCommand(Long tableId) {
        this(null, tableId);
    }
}
