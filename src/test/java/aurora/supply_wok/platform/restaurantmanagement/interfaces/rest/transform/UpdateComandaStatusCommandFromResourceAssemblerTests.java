package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.EComandaStatus;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateComandaStatusResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateComandaStatusCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenValidStatus_mapsStatusCorrectly() {
        // Arrange
        var resource = new UpdateComandaStatusResource("SERVED");

        // Act
        var command = UpdateComandaStatusCommandFromResourceAssembler.toCommandFromResource(10L, resource);

        // Assert
        assertThat(command.comandaId()).isEqualTo(10L);
        assertThat(command.status()).isEqualTo(EComandaStatus.SERVED);
    }

    @Test
    void toCommandFromResource_whenInvalidStatus_throwsIllegalArgumentException() {
        // Arrange
        var resource = new UpdateComandaStatusResource("UNKNOWN_STATUS");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> UpdateComandaStatusCommandFromResourceAssembler.toCommandFromResource(10L, resource));
    }
}
