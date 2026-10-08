package aurora.supply_wok.platform.alerts.domain.model.aggregates;

import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.events.AlertAcknowledgedEvent;
import aurora.supply_wok.platform.alerts.domain.model.events.AlertCreatedEvent;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantAlertTests {

    @Test
    void constructor_withFullParameters_setsPropertiesAndPendingStatus() {
        // Arrange
        var severity = EAlertSeverity.HIGH;
        var detail = "Temperature limit exceeded";
        Long sensorId = 15L;
        String sensorName = "Freezer A";

        // Act
        var alert = new RestaurantAlert(severity, detail, sensorId, sensorName);

        // Assert
        assertThat(alert.getSeverity()).isEqualTo(severity);
        assertThat(alert.getDetail()).isEqualTo(detail);
        assertThat(alert.getStatus()).isEqualTo(EAlertStatus.PENDING);
        assertThat(alert.getSensorId()).isEqualTo(sensorId);
        assertThat(alert.getSensorName()).isEqualTo(sensorName);
    }

    @Test
    void constructor_withCommandAndSensorId_setsSensorNameWithSensorPrefix() {
        // Arrange
        var command = new CreateRestaurantAlertCommand(EAlertSeverity.LOW, "Door open", 7L);

        // Act
        var alert = new RestaurantAlert(command);

        // Assert
        assertThat(alert.getSensorId()).isEqualTo(7L);
        assertThat(alert.getSensorName()).isEqualTo("Sensor 7");
        assertThat(alert.getStatus()).isEqualTo(EAlertStatus.PENDING);
    }

    @Test
    void constructor_withCommandAndNullSensorId_defaultsSensorIdZeroAndSensorNameSystem() {
        // Arrange
        var command = new CreateRestaurantAlertCommand(EAlertSeverity.CRITICAL, "System overload", null);

        // Act
        var alert = new RestaurantAlert(command);

        // Assert
        assertThat(alert.getSensorId()).isEqualTo(0L);
        assertThat(alert.getSensorName()).isEqualTo("System");
        assertThat(alert.getStatus()).isEqualTo(EAlertStatus.PENDING);
    }

    @Test
    void acknowledge_whenCalled_updatesStatusToAcknowledgedAndRegistersDomainEvent() {
        // Arrange
        var alert = new RestaurantAlert(EAlertSeverity.HIGH, "High pressure", 1L, "Sensor 1");

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
        var alert = new RestaurantAlert(EAlertSeverity.HIGH, "High pressure", 1L, "Sensor 1");

        // Act
        alert.onCreated();

        // Assert
        assertThat(alert.domainEvents()).hasSize(1);
        assertThat(alert.domainEvents().iterator().next()).isInstanceOf(AlertCreatedEvent.class);
    }
}
