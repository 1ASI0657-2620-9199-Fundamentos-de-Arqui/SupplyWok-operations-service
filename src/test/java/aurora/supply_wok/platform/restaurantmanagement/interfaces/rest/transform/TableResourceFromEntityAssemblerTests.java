package aurora.supply_wok.platform.restaurantmanagement.interfaces.rest.transform;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TableResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_whenValidEntity_mapsAllFields() {
        // Arrange
        var table = new RestaurantTable(3, 4);

        // Act
        var resource = TableResourceFromEntityAssembler.toResourceFromEntity(table);

        // Assert
        assertThat(resource.number()).isEqualTo(3);
        assertThat(resource.capacity()).isEqualTo(4);
        assertThat(resource.status()).isEqualTo("AVAILABLE");
    }
}
