package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

public record AddComandaItemCommand(Long comandaId, String productName, int quantity, String notes) {
}
