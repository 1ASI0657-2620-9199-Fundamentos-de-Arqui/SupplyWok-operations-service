package aurora.supply_wok.platform.alerts.domain.model.events;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;

/**
 * Domain event raised when an alert is created.
 */
public record AlertCreatedEvent(
        Long alertId,
        String alertType,
        String severity,
        String detail,
        String status
) {
    public static AlertCreatedEvent from(Alert alert) {
        return new AlertCreatedEvent(
                alert.getId(),
                resolveType(alert),
                alert.getSeverity().name(),
                alert.getDetail(),
                alert.getStatus().name()
        );
    }

    private static String resolveType(Alert alert) {
        if (alert instanceof RestaurantAlert) {
            return "RESTAURANT";
        }
        if (alert instanceof SupplierAlert) {
            return "SUPPLIER";
        }
        return "GENERAL";
    }
}
