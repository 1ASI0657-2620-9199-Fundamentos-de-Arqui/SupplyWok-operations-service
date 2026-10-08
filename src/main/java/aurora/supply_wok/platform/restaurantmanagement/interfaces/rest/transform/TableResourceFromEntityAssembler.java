package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.TableResource;

public class TableResourceFromEntityAssembler {

    public static TableResource toResourceFromEntity(RestaurantTable entity) {
        return new TableResource(
                entity.getId(),
                entity.getNumber(),
                entity.getCapacity(),
                entity.getStatus().name()
        );
    }
}
