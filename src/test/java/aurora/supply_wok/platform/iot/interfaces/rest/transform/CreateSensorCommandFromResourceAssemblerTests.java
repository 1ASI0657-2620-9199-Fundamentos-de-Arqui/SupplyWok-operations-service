package aurora.supply_wok.platform.iot.interfaces.rest.transform;

import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import aurora.supply_wok.platform.iot.interfaces.rest.resources.CreateSensorResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateSensorCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenValidResource_mapsAllFields() {
        // Arrange
        var resource = new CreateSensorResource("Temp", -10.0, 50.0, true, 22.5, "Temperature");

        // Act
        var command = CreateSensorCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.name()).isEqualTo("Temp");
        assertThat(command.minValue()).isEqualTo(-10.0);
        assertThat(command.maxValue()).isEqualTo(50.0);
        assertThat(command.enabled()).isTrue();
        assertThat(command.lastValue()).isEqualTo(22.5);
        assertThat(command.type()).isEqualTo(ESensorType.Temperature);
    }

    @Test
    void toCommandFromResource_whenLastValueNull_defaultsToZero() {
        // Arrange
        var resource = new CreateSensorResource("Temp", -10.0, 50.0, true, null, "Temperature");

        // Act
        var command = CreateSensorCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.lastValue()).isZero();
    }

    @Test
    void toCommandFromResource_whenInvalidType_throwsIllegalArgumentException() {
        // Arrange
        var resource = new CreateSensorResource("Temp", -10.0, 50.0, true, 20.0, "INVALID_TYPE");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> CreateSensorCommandFromResourceAssembler.toCommandFromResource(resource));
    }
}
