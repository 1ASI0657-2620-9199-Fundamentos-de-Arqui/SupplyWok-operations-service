package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

public record DeleteComandaCommand(Long restaurantId, Long comandaId) {
    public DeleteComandaCommand(Long comandaId) {
        this(null, comandaId);
    }
}
