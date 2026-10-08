package aurora.supply_wok.platform.alerts.domain.model.aggregates;

import aurora.supply_wok.platform.alerts.domain.model.commands.CreateSupplierAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUPPLIER")
public class SupplierAlert extends Alert {

    public SupplierAlert() {
        super();
    }

    public SupplierAlert(EAlertSeverity severity, String detail) {
        super(severity, detail);
    }

    public SupplierAlert(CreateSupplierAlertCommand command) {
        super(command.severity(), command.detail());
    }
}
