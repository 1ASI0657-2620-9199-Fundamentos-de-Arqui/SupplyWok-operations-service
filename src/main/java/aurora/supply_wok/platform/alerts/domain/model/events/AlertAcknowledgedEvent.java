package aurora.supply_wok.platform.alerts.domain.model.events;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;

/**
 * Domain event raised when an alert is acknowledged.
 */
public record AlertAcknowledgedEvent(
        Long alertId,
        String severity,
        String detail
) {
    public static AlertAcknowledgedEvent from(Alert alert) {
        return new AlertAcknowledgedEvent(alert.getId(), alert.getSeverity().name(), alert.getDetail());
    }
}
