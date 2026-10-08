package aurora.supply_wok.platform.restaurantmanagement.domain.model.events;

public record ComandaItemAddedEvent(
        Long comandaId,
        Long tableId,
        String productName,
        int quantity
) {
}
