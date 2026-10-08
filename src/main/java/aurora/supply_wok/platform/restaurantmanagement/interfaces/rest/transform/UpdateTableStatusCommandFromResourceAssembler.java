package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateTableStatusResource;

public class UpdateTableStatusCommandFromResourceAssembler {

    public static UpdateTableStatusCommand toCommandFromResource(Long tableId, UpdateTableStatusResource resource) {
        ETableStatus status;
        try {
            status = ETableStatus.valueOf(resource.status());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid table status: " + resource.status());
        }
        return new UpdateTableStatusCommand(tableId, status);
    }
}
