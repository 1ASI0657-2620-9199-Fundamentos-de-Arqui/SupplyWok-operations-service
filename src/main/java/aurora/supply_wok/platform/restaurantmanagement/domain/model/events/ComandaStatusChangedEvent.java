package aurora.supply_wok.platform.restaurantmanagement.domain.model.events;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.EComandaStatus;

public record ComandaStatusChangedEvent(
        Long comandaId,
        Long tableId,
        EComandaStatus previousStatus,
        EComandaStatus currentStatus
) {
    public static ComandaStatusChangedEvent from(Comanda comanda, EComandaStatus previousStatus) {
        return new ComandaStatusChangedEvent(comanda.getId(), comanda.getTableId(), previousStatus, comanda.getStatus());
    }
}
