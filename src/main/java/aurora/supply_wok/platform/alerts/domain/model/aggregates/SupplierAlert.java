package aurora.supply_wok.platform.alerts.domain.model.aggregates;

import aurora.supply_wok.platform.alerts.domain.model.commands.CreateSupplierAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@DiscriminatorValue("SUPPLIER")
public class SupplierAlert extends Alert {

    @Getter
    @Column(name = "supplier_email", nullable = true)
    private String supplierEmail;

    public SupplierAlert() {
        super();
    }

    public SupplierAlert(String supplierEmail, EAlertSeverity severity, String detail) {
        super(severity, detail);
        this.supplierEmail = supplierEmail;
    }

    public SupplierAlert(EAlertSeverity severity, String detail) {
        this(null, severity, detail);
    }

    public SupplierAlert(CreateSupplierAlertCommand command) {
        super(command.severity(), command.detail());
        this.supplierEmail = command.supplierEmail();
    }
}
