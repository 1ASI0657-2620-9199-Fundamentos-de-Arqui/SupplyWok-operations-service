package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.EComandaStatus;

public record UpdateComandaStatusCommand(Long restaurantId, Long comandaId, EComandaStatus status) {
    public UpdateComandaStatusCommand(Long comandaId, EComandaStatus status) {
        this(null, comandaId, status);
    }
}
