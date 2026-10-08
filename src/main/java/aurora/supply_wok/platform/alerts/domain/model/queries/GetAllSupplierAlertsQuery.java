package aurora.supply_wok.platform.alerts.domain.model.queries;

public record GetAllSupplierAlertsQuery(String supplierEmail) {
    public GetAllSupplierAlertsQuery() {
        this(null);
    }
}
