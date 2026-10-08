package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.AddComandaItemCommand;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComandaResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_whenValidEntity_mapsAllFieldsAndItems() {
        // Arrange
        var comanda = new Comanda(2L);
        comanda.addItem(new AddComandaItemCommand(1L, "Wok Noodles", 2, "Spicy"));

        // Act
        var resource = ComandaResourceFromEntityAssembler.toResourceFromEntity(comanda);

        // Assert
        assertThat(resource.tableId()).isEqualTo(2L);
        assertThat(resource.status()).isEqualTo("OPEN");
        assertThat(resource.items()).hasSize(1);
        assertThat(resource.items().getFirst().productName()).isEqualTo("Wok Noodles");
        assertThat(resource.items().getFirst().quantity()).isEqualTo(2);
        assertThat(resource.items().getFirst().notes()).isEqualTo("Spicy");
    }
}
