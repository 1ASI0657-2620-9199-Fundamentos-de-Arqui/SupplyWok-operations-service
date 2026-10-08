package aurora.supply_wok.platform.alerts.domain.model.commands;

import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;

public record CreateSupplierAlertCommand(String supplierEmail, EAlertSeverity severity, String detail) {
    public CreateSupplierAlertCommand(EAlertSeverity severity, String detail) {
        this(null, severity, detail);
    }
}
