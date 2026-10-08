package aurora.supply_wok.platform.alerts.domain.model.commands;

import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;

public record CreateRestaurantAlertCommand(Long restaurantId, EAlertSeverity severity, String detail, Long sensorId) {
    public CreateRestaurantAlertCommand(EAlertSeverity severity, String detail, Long sensorId) {
        this(null, severity, detail, sensorId);
    }
}
