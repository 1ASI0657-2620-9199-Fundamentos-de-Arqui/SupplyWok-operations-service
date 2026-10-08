package aurora.supply_wok.platform.alerts.domain.model.commands;

import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;

public record CreateSupplierAlertCommand(EAlertSeverity severity, String detail) {
}
