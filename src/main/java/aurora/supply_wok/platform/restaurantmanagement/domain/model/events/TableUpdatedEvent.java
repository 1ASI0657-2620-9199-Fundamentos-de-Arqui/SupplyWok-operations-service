package aurora.supply_wok.platform.restaurantmanagement.domain.model.events;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;

public record TableUpdatedEvent(Long tableId, int number, int capacity, String status) {
    public static TableUpdatedEvent from(RestaurantTable table) {
        return new TableUpdatedEvent(table.getId(), table.getNumber(), table.getCapacity(), table.getStatus().name());
    }
}
