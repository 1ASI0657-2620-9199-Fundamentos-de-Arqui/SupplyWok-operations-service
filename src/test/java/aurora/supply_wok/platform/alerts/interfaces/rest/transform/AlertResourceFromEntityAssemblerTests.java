package aurora.supply_wok.platform.alerts.interfaces.rest.transform;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AlertResourceFromEntityAssemblerTests {

    @Test
    void toRestaurantResourceFromEntity_whenSensorIdPresent_mapsFields() {
        // Arrange
        var alert = new RestaurantAlert(EAlertSeverity.HIGH, "High pressure", 3L, "Pressure Sensor");

        // Act
        var resource = AlertResourceFromEntityAssembler.toRestaurantResourceFromEntity(alert);

        // Assert
        assertThat(resource.severity()).isEqualTo("HIGH");
        assertThat(resource.detail()).isEqualTo("High pressure");
        assertThat(resource.status()).isEqualTo("PENDING");
        assertThat(resource.alertType()).isEqualTo("RESTAURANT");
        assertThat(resource.sensorId()).isEqualTo(3L);
        assertThat(resource.sensorName()).isEqualTo("Pressure Sensor");
    }

    @Test
    void toRestaurantResourceFromEntity_whenSensorIdZero_mapsSensorIdAsNull() {
        // Arrange
        var alert = new RestaurantAlert(EAlertSeverity.LOW, "System check", 0L, "System");

        // Act
        var resource = AlertResourceFromEntityAssembler.toRestaurantResourceFromEntity(alert);

        // Assert
        assertThat(resource.sensorId()).isNull();
        assertThat(resource.sensorName()).isEqualTo("System");
    }

    @Test
    void toSupplierResourceFromEntity_whenValidEntity_mapsAllFields() {
        // Arrange
        var alert = new SupplierAlert(EAlertSeverity.MEDIUM, "Stock delayed");

        // Act
        var resource = AlertResourceFromEntityAssembler.toSupplierResourceFromEntity(alert);

        // Assert
        assertThat(resource.severity()).isEqualTo("MEDIUM");
        assertThat(resource.detail()).isEqualTo("Stock delayed");
        assertThat(resource.status()).isEqualTo("PENDING");
        assertThat(resource.alertType()).isEqualTo("SUPPLIER");
    }
}
