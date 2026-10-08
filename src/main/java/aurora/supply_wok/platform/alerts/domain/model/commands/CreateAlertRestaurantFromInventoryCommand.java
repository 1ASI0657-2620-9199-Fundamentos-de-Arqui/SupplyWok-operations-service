package aurora.supply_wok.platform.alerts.domain.model.commands;

/**
 * Command to create a restaurant alert when inventory stock differs from the sensor last value.
 */
public record CreateAlertRestaurantFromInventoryCommand(Long sensorId) {
}
