package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

public record UpdateTableCommand(Long tableId, int number, int capacity) {
}
