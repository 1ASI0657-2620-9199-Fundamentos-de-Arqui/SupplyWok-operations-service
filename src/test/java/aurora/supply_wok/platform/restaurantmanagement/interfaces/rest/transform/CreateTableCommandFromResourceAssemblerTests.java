package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.CreateTableResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTableCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenValidResource_mapsNumberAndCapacity() {
        // Arrange
        var resource = new CreateTableResource(5, 6);

        // Act
        var command = CreateTableCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.number()).isEqualTo(5);
        assertThat(command.capacity()).isEqualTo(6);
    }
}
