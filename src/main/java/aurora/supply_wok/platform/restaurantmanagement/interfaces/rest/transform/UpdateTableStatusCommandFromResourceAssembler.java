package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateTableStatusResource;

public class UpdateTableStatusCommandFromResourceAssembler {

    public static UpdateTableStatusCommand toCommandFromResource(Long tableId, UpdateTableStatusResource resource) {
        return new UpdateTableStatusCommand(null, tableId, ETableStatus.valueOf(resource.status()));
    }

    public static UpdateTableStatusCommand toCommandFromResource(Long restaurantId, Long tableId, UpdateTableStatusResource resource) {
        return new UpdateTableStatusCommand(restaurantId, tableId, ETableStatus.valueOf(resource.status()));
    }
}
