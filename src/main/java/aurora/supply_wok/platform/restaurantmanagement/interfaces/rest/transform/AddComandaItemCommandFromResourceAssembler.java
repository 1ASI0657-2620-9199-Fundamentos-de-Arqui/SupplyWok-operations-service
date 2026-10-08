package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.AddComandaItemCommand;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.AddComandaItemResource;

public class AddComandaItemCommandFromResourceAssembler {

    public static AddComandaItemCommand toCommandFromResource(Long comandaId, AddComandaItemResource resource) {
        return new AddComandaItemCommand(
                comandaId,
                resource.productName(),
                resource.quantity(),
                resource.notes()
        );
    }
}
