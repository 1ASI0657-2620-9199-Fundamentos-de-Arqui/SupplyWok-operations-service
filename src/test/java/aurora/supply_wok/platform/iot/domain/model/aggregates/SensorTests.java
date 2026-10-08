package aurora.supply_wok.platform.iot.domain.model.aggregates;

import aurora.supply_wok.platform.iot.domain.model.commands.CreateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.commands.UpdateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.events.SensorCreatedEvent;
import aurora.supply_wok.platform.iot.domain.model.events.SensorThresholdBreachedEvent;
import aurora.supply_wok.platform.iot.domain.model.events.SensorUpdatedEvent;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SensorTests {

    @Test
    void constructor_withCreateSensorCommand_initializesFields() {
        // Arrange
        var command = new CreateSensorCommand("Cooler Temp", -5.0, 5.0, true, 2.0, ESensorType.Temperature);

        // Act
        var sensor = new Sensor(command);

        // Assert
        assertThat(sensor.getName()).isEqualTo("Cooler Temp");
        assertThat(sensor.getMinValue()).isEqualTo(-5.0);
        assertThat(sensor.getMaxValue()).isEqualTo(5.0);
        assertThat(sensor.isEnabled()).isTrue();
        assertThat(sensor.getLastValue()).isEqualTo(2.0);
        assertThat(sensor.getType()).isEqualTo(ESensorType.Temperature);
    }

    @Test
    void onCreated_whenValueWithinBounds_registersSensorCreatedEventOnly() {
        // Arrange
        var sensor = new Sensor("Cooler Temp", -5.0, 5.0, true, 0.0, ESensorType.Temperature);

        // Act
        sensor.onCreated();

        // Assert
        assertThat(sensor.domainEvents()).hasSize(1);
        assertThat(sensor.domainEvents().iterator().next()).isInstanceOf(SensorCreatedEvent.class);
    }

    @Test
    void onCreated_whenValueBelowMinimumAndEnabled_registersThresholdBreachedEvent() {
        // Arrange
        var sensor = new Sensor("Cooler Temp", -5.0, 5.0, true, -10.0, ESensorType.Temperature);

        // Act
        sensor.onCreated();

        // Assert
        assertThat(sensor.domainEvents()).hasSize(2);
        assertThat(sensor.domainEvents()).anyMatch(e -> e instanceof SensorThresholdBreachedEvent);
    }

    @Test
    void onCreated_whenValueAboveMaximumAndEnabled_registersThresholdBreachedEvent() {
        // Arrange
        var sensor = new Sensor("Cooler Temp", -5.0, 5.0, true, 8.0, ESensorType.Temperature);

        // Act
        sensor.onCreated();

        // Assert
        assertThat(sensor.domainEvents()).hasSize(2);
        assertThat(sensor.domainEvents()).anyMatch(e -> e instanceof SensorThresholdBreachedEvent);
    }

    @Test
    void onCreated_whenValueOutOfBoundsButDisabled_doesNotRegisterThresholdBreachedEvent() {
        // Arrange
        var sensor = new Sensor("Cooler Temp", -5.0, 5.0, false, 8.0, ESensorType.Temperature);

        // Act
        sensor.onCreated();

        // Assert
        assertThat(sensor.domainEvents()).hasSize(1);
        assertThat(sensor.domainEvents().iterator().next()).isInstanceOf(SensorCreatedEvent.class);
    }

    @Test
    void updateSensor_whenValueWithinBounds_registersOnlySensorUpdatedEvent() {
        // Arrange
        var sensor = new Sensor("Cooler Temp", -5.0, 5.0, true, 0.0, ESensorType.Temperature);
        var updateCommand = new UpdateSensorCommand(1L, "Cooler Temp", -5.0, 5.0, true, 1.5, ESensorType.Temperature);

        // Act
        sensor.updateSensor(updateCommand);

        // Assert
        assertThat(sensor.getLastValue()).isEqualTo(1.5);
        assertThat(sensor.domainEvents()).hasSize(1);
        assertThat(sensor.domainEvents().iterator().next()).isInstanceOf(SensorUpdatedEvent.class);
    }

    @Test
    void updateSensor_whenValueExceedsThreshold_registersUpdatedAndBreachedEvents() {
        // Arrange
        var sensor = new Sensor("Cooler Temp", -5.0, 5.0, true, 0.0, ESensorType.Temperature);
        var updateCommand = new UpdateSensorCommand(1L, "Cooler Temp", -5.0, 5.0, true, 12.0, ESensorType.Temperature);

        // Act
        sensor.updateSensor(updateCommand);

        // Assert
        assertThat(sensor.getLastValue()).isEqualTo(12.0);
        assertThat(sensor.domainEvents()).hasSize(2);
        assertThat(sensor.domainEvents()).anyMatch(e -> e instanceof SensorThresholdBreachedEvent);
    }
}
