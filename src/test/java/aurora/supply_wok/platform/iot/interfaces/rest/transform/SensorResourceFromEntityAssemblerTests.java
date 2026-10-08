package aurora.supply_wok.platform.iot.interfaces.rest.transform;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SensorResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_whenValidEntity_mapsAllFields() {
        // Arrange
        var sensor = new Sensor("Humidity", 10.0, 90.0, true, 45.0, ESensorType.Humidity);

        // Act
        var resource = SensorResourceFromEntityAssembler.toResourceFromEntity(sensor);

        // Assert
        assertThat(resource.name()).isEqualTo("Humidity");
        assertThat(resource.minValue()).isEqualTo(10.0);
        assertThat(resource.maxValue()).isEqualTo(90.0);
        assertThat(resource.enabled()).isTrue();
        assertThat(resource.lastValue()).isEqualTo(45.0);
        assertThat(resource.type()).isEqualTo("Humidity");
    }
}
