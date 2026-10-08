package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.ComandaItemResource;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.ComandaResource;

public class ComandaResourceFromEntityAssembler {

    public static ComandaResource toResourceFromEntity(Comanda entity) {
        var items = entity.getItems().stream()
                .map(item -> new ComandaItemResource(
                        item.getId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getNotes()
                ))
                .toList();

        return new ComandaResource(
                entity.getId(),
                entity.getTableId(),
                entity.getStatus().name(),
                items
        );
    }
}
