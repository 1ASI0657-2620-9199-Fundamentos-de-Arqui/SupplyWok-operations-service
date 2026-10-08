package aurora.supply_wok.platform.restaurantmanagement.domain.model.services;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.AddComandaItemCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateComandaCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.DeleteComandaCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateComandaStatusCommand;

import java.util.Optional;

public interface ComandaCommandService {
    Long handle(CreateComandaCommand command);
    Optional<Comanda> handle(UpdateComandaStatusCommand command);
    Optional<Comanda> handle(AddComandaItemCommand command);
    void handle(DeleteComandaCommand command);
}
