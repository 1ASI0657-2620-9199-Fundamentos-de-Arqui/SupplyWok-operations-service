package aurora.supply_wok.platform.iot.interfaces.rest.transform;

import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import aurora.supply_wok.platform.iot.interfaces.rest.resources.UpdateSensorResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSensorCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenValidResource_mapsAllFields() {
        // Arrange
        var resource = new UpdateSensorResource("Humidity", 10.0, 90.0, true, 50.0, "Humidity");

        // Act
        var command = UpdateSensorCommandFromResourceAssembler.toCommandFromResource(4L, resource);

        // Assert
        assertThat(command.sensorId()).isEqualTo(4L);
        assertThat(command.name()).isEqualTo("Humidity");
        assertThat(command.minValue()).isEqualTo(10.0);
        assertThat(command.maxValue()).isEqualTo(90.0);
        assertThat(command.enabled()).isTrue();
        assertThat(command.lastValue()).isEqualTo(50.0);
        assertThat(command.type()).isEqualTo(ESensorType.Humidity);
    }

    @Test
    void toCommandFromResource_whenLastValueNull_defaultsToZero() {
        // Arrange
        var resource = new UpdateSensorResource("Humidity", 10.0, 90.0, true, null, "Humidity");

        // Act
        var command = UpdateSensorCommandFromResourceAssembler.toCommandFromResource(4L, resource);

        // Assert
        assertThat(command.lastValue()).isZero();
    }

    @Test
    void toCommandFromResource_whenInvalidType_throwsIllegalArgumentException() {
        // Arrange
        var resource = new UpdateSensorResource("Humidity", 10.0, 90.0, true, 50.0, "UNKNOWN");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> UpdateSensorCommandFromResourceAssembler.toCommandFromResource(4L, resource));
    }
}
