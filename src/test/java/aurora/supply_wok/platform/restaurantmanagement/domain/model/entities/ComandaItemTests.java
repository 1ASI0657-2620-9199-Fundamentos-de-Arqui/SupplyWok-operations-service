package aurora.supply_wok.platform.restaurantmanagement.domain.model.entities;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComandaItemTests {

    @Test
    void constructor_withParameters_initializesFields() {
        // Arrange
        var comanda = new Comanda(1L);

        // Act
        var item = new ComandaItem("Spring Rolls", 3, "No spicy sauce", comanda);

        // Assert
        assertThat(item.getProductName()).isEqualTo("Spring Rolls");
        assertThat(item.getQuantity()).isEqualTo(3);
        assertThat(item.getNotes()).isEqualTo("No spicy sauce");
    }

    @Test
    void updateQuantity_whenValidQuantity_updatesField() {
        // Arrange
        var item = new ComandaItem("Spring Rolls", 3, "No spicy sauce", new Comanda(1L));

        // Act
        item.updateQuantity(5);

        // Assert
        assertThat(item.getQuantity()).isEqualTo(5);
    }
}
