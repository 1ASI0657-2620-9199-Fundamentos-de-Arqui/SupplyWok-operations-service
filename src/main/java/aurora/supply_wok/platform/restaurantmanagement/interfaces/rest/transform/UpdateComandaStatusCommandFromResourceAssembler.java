package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateComandaStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.EComandaStatus;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateComandaStatusResource;

public class UpdateComandaStatusCommandFromResourceAssembler {

    public static UpdateComandaStatusCommand toCommandFromResource(Long comandaId, UpdateComandaStatusResource resource) {
        EComandaStatus status;
        try {
            status = EComandaStatus.valueOf(resource.status());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid comanda status: " + resource.status());
        }
        return new UpdateComandaStatusCommand(comandaId, status);
    }
}
