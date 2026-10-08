package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.EComandaStatus;

public record UpdateComandaStatusCommand(Long comandaId, EComandaStatus status) {
}
