package aurora.supply_wok.platform.alerts.domain.model.commands;

/**
 * Command to acknowledge an alert.
 */
public record AcknowledgeAlertCommand(Long alertId, Long restaurantId, String supplierEmail) {
    public AcknowledgeAlertCommand(Long alertId) {
        this(alertId, null, null);
    }

    public AcknowledgeAlertCommand(Long alertId, Long restaurantId) {
        this(alertId, restaurantId, null);
    }

    public AcknowledgeAlertCommand(Long alertId, String supplierEmail) {
        this(alertId, null, supplierEmail);
    }
}
