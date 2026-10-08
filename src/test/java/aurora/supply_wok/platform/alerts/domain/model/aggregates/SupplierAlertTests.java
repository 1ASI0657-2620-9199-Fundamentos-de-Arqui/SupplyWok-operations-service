package aurora.supply_wok.platform.alerts.domain.model.aggregates;

import aurora.supply_wok.platform.alerts.domain.model.commands.CreateSupplierAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.events.AlertAcknowledgedEvent;
import aurora.supply_wok.platform.alerts.domain.model.events.AlertCreatedEvent;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SupplierAlertTests {

    @Test
    void constructor_withSeverityAndDetail_initializesWithPendingStatus() {
        // Arrange
        var severity = EAlertSeverity.MEDIUM;
        var detail = "Order delayed";

        // Act
        var alert = new SupplierAlert(severity, detail);

        // Assert
        assertThat(alert.getSeverity()).isEqualTo(severity);
        assertThat(alert.getDetail()).isEqualTo(detail);
        assertThat(alert.getStatus()).isEqualTo(EAlertStatus.PENDING);
    }

    @Test
    void constructor_withCreateSupplierAlertCommand_initializesProperties() {
        // Arrange
        var command = new CreateSupplierAlertCommand(EAlertSeverity.CRITICAL, "Supplier unavailable");

        // Act
        var alert = new SupplierAlert(command);

        // Assert
        assertThat(alert.getSeverity()).isEqualTo(EAlertSeverity.CRITICAL);
        assertThat(alert.getDetail()).isEqualTo("Supplier unavailable");
        assertThat(alert.getStatus()).isEqualTo(EAlertStatus.PENDING);
    }

    @Test
    void acknowledge_whenCalled_updatesStatusToAcknowledgedAndRegistersDomainEvent() {
        // Arrange
        var alert = new SupplierAlert(EAlertSeverity.MEDIUM, "Order delayed");

        // Act
        alert.acknowledge();

        // Assert
        assertThat(alert.getStatus()).isEqualTo(EAlertStatus.ACKNOWLEDGED);
        assertThat(alert.domainEvents()).hasSize(1);
        assertThat(alert.domainEvents().iterator().next()).isInstanceOf(AlertAcknowledgedEvent.class);
    }

    @Test
    void onCreated_whenCalled_registersAlertCreatedEvent() {
        // Arrange
        var alert = new SupplierAlert(EAlertSeverity.LOW, "Shipment ready");

        // Act
        alert.onCreated();

        // Assert
        assertThat(alert.domainEvents()).hasSize(1);
        assertThat(alert.domainEvents().iterator().next()).isInstanceOf(AlertCreatedEvent.class);
    }
}
