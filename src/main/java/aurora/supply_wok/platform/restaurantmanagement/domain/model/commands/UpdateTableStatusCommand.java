package aurora.supply_wok.platform.restaurantmanagement.domain.model.commands;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;

public record UpdateTableStatusCommand(Long restaurantId, Long tableId, ETableStatus status) {
    public UpdateTableStatusCommand(Long tableId, ETableStatus status) {
        this(null, tableId, status);
    }
}
