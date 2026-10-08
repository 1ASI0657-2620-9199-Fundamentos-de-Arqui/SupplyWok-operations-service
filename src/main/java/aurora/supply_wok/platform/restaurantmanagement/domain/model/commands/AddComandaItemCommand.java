package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

public record AddComandaItemCommand(Long restaurantId, Long comandaId, String productName, int quantity, String notes) {
    public AddComandaItemCommand(Long comandaId, String productName, int quantity, String notes) {
        this(null, comandaId, productName, quantity, notes);
    }
}
