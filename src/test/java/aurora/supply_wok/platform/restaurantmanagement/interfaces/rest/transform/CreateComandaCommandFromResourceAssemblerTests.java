package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.CreateComandaResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateComandaCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenValidResource_mapsTableId() {
        // Arrange
        var resource = new CreateComandaResource(8L);

        // Act
        var command = CreateComandaCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.tableId()).isEqualTo(8L);
    }
}
