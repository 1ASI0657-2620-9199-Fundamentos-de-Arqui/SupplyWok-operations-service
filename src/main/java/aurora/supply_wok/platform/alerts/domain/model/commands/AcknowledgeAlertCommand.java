package aurora.supply_wok.platform.alerts.domain.model.commands;

/**
 * Command to acknowledge an alert.
 */
public record AcknowledgeAlertCommand(Long alertId) {
}
