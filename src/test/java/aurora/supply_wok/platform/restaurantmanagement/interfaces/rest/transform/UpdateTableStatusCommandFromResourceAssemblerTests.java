package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;
import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateTableStatusResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateTableStatusCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenValidStatus_mapsStatusCorrectly() {
        // Arrange
        var resource = new UpdateTableStatusResource("OCCUPIED");

        // Act
        var command = UpdateTableStatusCommandFromResourceAssembler.toCommandFromResource(12L, resource);

        // Assert
        assertThat(command.tableId()).isEqualTo(12L);
        assertThat(command.status()).isEqualTo(ETableStatus.OCCUPIED);
    }

    @Test
    void toCommandFromResource_whenInvalidStatus_throwsIllegalArgumentException() {
        // Arrange
        var resource = new UpdateTableStatusResource("INVALID");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> UpdateTableStatusCommandFromResourceAssembler.toCommandFromResource(12L, resource));
    }
}
