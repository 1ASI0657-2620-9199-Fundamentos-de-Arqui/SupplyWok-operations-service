package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.resources.AddComandaItemResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddComandaItemCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenValidResource_mapsAllFields() {
        // Arrange
        var resource = new AddComandaItemResource("Dumplings", 5, "Steamed");

        // Act
        var command = AddComandaItemCommandFromResourceAssembler.toCommandFromResource(10L, resource);

        // Assert
        assertThat(command.comandaId()).isEqualTo(10L);
        assertThat(command.productName()).isEqualTo("Dumplings");
        assertThat(command.quantity()).isEqualTo(5);
        assertThat(command.notes()).isEqualTo("Steamed");
    }
}
