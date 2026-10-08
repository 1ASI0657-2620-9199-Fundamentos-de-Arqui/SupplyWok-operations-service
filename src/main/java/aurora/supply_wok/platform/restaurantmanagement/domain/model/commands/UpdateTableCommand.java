package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

public record UpdateTableCommand(Long restaurantId, Long tableId, int number, int capacity) {
    public UpdateTableCommand(Long tableId, int number, int capacity) {
        this(null, tableId, number, capacity);
    }
}
