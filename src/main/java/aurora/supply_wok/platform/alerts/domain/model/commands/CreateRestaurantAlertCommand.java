package aurora.supply_wok.platform.alerts.domain.model.commands;

import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;

public record CreateRestaurantAlertCommand(EAlertSeverity severity, String detail, Long sensorId) {
}
