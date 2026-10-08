package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;

public record UpdateTableStatusCommand(Long tableId, ETableStatus status) {
}
