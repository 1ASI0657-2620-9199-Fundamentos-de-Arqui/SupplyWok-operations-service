package aurora.supply_wok.platform.restaurantmanagement.domain.model.events;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;

public record TableCreatedEvent(Long tableId, int number, int capacity, String status) {
    public static TableCreatedEvent from(RestaurantTable table) {
        return new TableCreatedEvent(table.getId(), table.getNumber(), table.getCapacity(), table.getStatus().name());
    }
}
