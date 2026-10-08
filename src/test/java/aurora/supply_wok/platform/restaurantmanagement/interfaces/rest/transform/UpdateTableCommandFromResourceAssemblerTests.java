package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.UpdateTableResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateTableCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenValidResource_mapsAllFields() {
        // Arrange
        var resource = new UpdateTableResource(7, 8);

        // Act
        var command = UpdateTableCommandFromResourceAssembler.toCommandFromResource(12L, resource);

        // Assert
        assertThat(command.tableId()).isEqualTo(12L);
        assertThat(command.number()).isEqualTo(7);
        assertThat(command.capacity()).isEqualTo(8);
    }
}
