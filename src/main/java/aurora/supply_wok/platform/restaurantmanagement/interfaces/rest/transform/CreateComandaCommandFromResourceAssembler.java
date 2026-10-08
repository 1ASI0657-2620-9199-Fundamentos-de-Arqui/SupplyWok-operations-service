package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateComandaCommand;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.CreateComandaResource;

public class CreateComandaCommandFromResourceAssembler {

    public static CreateComandaCommand toCommandFromResource(CreateComandaResource resource) {
        return new CreateComandaCommand(resource.tableId());
    }
}
