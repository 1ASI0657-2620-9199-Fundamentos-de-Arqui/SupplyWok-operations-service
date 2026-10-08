package aurora.supply_wok.platform.restaurantmanagement.domain.model.events;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;

public record ComandaCreatedEvent(Long comandaId, Long tableId, String status) {
    public static ComandaCreatedEvent from(Comanda comanda) {
        return new ComandaCreatedEvent(comanda.getId(), comanda.getTableId(), comanda.getStatus().name());
    }
}
