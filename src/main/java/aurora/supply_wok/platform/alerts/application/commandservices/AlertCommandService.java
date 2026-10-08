package aurora.supply_wok.platform.alerts.application.commandservices;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;
import aurora.supply_wok.platform.alerts.domain.model.commands.AcknowledgeAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateAlertRestaurantFromInventoryCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateSupplierAlertCommand;

import java.util.Optional;

public interface AlertCommandService {
    Long handle(CreateSupplierAlertCommand command);
    Long handle(CreateRestaurantAlertCommand command);
    Optional<Alert> handle(CreateAlertRestaurantFromInventoryCommand command);
    Optional<Alert> handle(AcknowledgeAlertCommand command);
}
