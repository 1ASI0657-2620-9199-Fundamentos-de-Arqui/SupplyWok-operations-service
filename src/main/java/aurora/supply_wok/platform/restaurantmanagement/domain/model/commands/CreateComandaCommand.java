package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

public record CreateComandaCommand(Long restaurantId, Long tableId) {
    public CreateComandaCommand(Long tableId) {
        this(1L, tableId);
    }
}
