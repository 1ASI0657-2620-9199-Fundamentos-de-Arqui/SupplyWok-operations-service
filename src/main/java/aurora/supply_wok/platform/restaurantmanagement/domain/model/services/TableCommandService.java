package aurora.supply_wok.platform.restaurantmanagement.domain.model.services;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.DeleteTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand;

import java.util.Optional;

public interface TableCommandService {
    Long handle(CreateTableCommand command);
    Optional<RestaurantTable> handle(UpdateTableCommand command);
    Optional<RestaurantTable> handle(UpdateTableStatusCommand command);
    void handle(DeleteTableCommand command);
}
