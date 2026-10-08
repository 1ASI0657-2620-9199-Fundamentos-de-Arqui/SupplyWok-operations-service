package aurora.supply_wok.platform.alerts.domain.model.queries;

public record GetAlertByIdQuery(Long alertId, Long restaurantId, String supplierEmail) {
    public GetAlertByIdQuery(Long alertId) {
        this(alertId, null, null);
    }

    public GetAlertByIdQuery(Long alertId, Long restaurantId) {
        this(alertId, restaurantId, null);
    }

    public GetAlertByIdQuery(Long alertId, String supplierEmail) {
        this(alertId, null, supplierEmail);
    }
}
