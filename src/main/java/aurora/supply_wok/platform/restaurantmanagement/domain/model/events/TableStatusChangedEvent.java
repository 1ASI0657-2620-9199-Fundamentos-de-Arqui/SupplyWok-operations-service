package aurora.supply_wok.platform.restaurantmanagement.domain.model.events;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;

public record TableStatusChangedEvent(
        Long tableId,
        int number,
        ETableStatus previousStatus,
        ETableStatus currentStatus
) {
    public static TableStatusChangedEvent from(RestaurantTable table, ETableStatus previousStatus) {
        return new TableStatusChangedEvent(table.getId(), table.getNumber(), previousStatus, table.getStatus());
    }
}
