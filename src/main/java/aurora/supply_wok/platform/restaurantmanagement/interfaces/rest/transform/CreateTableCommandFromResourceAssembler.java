package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.CreateTableResource;

public class CreateTableCommandFromResourceAssembler {

    public static CreateTableCommand toCommandFromResource(CreateTableResource resource) {
        return new CreateTableCommand(resource.number(), resource.capacity());
    }
}
