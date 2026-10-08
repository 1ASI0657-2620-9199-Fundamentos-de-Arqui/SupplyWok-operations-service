package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateTableResource;

public class UpdateTableCommandFromResourceAssembler {

    public static UpdateTableCommand toCommandFromResource(Long tableId, UpdateTableResource resource) {
        return new UpdateTableCommand(null, tableId, resource.number(), resource.capacity());
    }

    public static UpdateTableCommand toCommandFromResource(Long restaurantId, Long tableId, UpdateTableResource resource) {
        return new UpdateTableCommand(restaurantId, tableId, resource.number(), resource.capacity());
    }
}
