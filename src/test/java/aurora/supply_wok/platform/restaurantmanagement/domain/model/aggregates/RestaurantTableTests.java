package aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.TableCreatedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.TableStatusChangedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.TableUpdatedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantTableTests {

    @Test
    void constructor_withNumberAndCapacity_initializesWithAvailableStatus() {
        // Arrange
        int number = 4;
        int capacity = 6;

        // Act
        var table = new RestaurantTable(number, capacity);

        // Assert
        assertThat(table.getNumber()).isEqualTo(number);
        assertThat(table.getCapacity()).isEqualTo(capacity);
        assertThat(table.getStatus()).isEqualTo(ETableStatus.AVAILABLE);
    }

    @Test
    void constructor_withCreateTableCommand_initializesProperties() {
        // Arrange
        var command = new CreateTableCommand(12, 4);

        // Act
        var table = new RestaurantTable(command);

        // Assert
        assertThat(table.getNumber()).isEqualTo(12);
        assertThat(table.getCapacity()).isEqualTo(4);
        assertThat(table.getStatus()).isEqualTo(ETableStatus.AVAILABLE);
    }

    @Test
    void updateStatus_whenStatusDiffers_updatesStatusAndRegistersDomainEvent() {
        // Arrange
        var table = new RestaurantTable(1, 4);
        var command = new UpdateTableStatusCommand(1L, ETableStatus.OCCUPIED);

        // Act
        table.updateStatus(command);

        // Assert
        assertThat(table.getStatus()).isEqualTo(ETableStatus.OCCUPIED);
        assertThat(table.domainEvents()).hasSize(1);
        assertThat(table.domainEvents().iterator().next()).isInstanceOf(TableStatusChangedEvent.class);
    }

    @Test
    void updateStatus_whenStatusIsIdentical_doesNotRegisterDomainEvent() {
        // Arrange
        var table = new RestaurantTable(1, 4);
        var command = new UpdateTableStatusCommand(1L, ETableStatus.AVAILABLE);

        // Act
        table.updateStatus(command);

        // Assert
        assertThat(table.getStatus()).isEqualTo(ETableStatus.AVAILABLE);
        assertThat(table.domainEvents()).isEmpty();
    }

    @Test
    void update_whenValidCommand_updatesNumberCapacityAndRegistersEvent() {
        // Arrange
        var table = new RestaurantTable(1, 4);
        var command = new UpdateTableCommand(1L, 2, 8);

        // Act
        table.update(command);

        // Assert
        assertThat(table.getNumber()).isEqualTo(2);
        assertThat(table.getCapacity()).isEqualTo(8);
        assertThat(table.domainEvents()).hasSize(1);
        assertThat(table.domainEvents().iterator().next()).isInstanceOf(TableUpdatedEvent.class);
    }

    @Test
    void onCreated_whenCalled_registersTableCreatedEvent() {
        // Arrange
        var table = new RestaurantTable(1, 4);

        // Act
        table.onCreated();

        // Assert
        assertThat(table.domainEvents()).hasSize(1);
        assertThat(table.domainEvents().iterator().next()).isInstanceOf(TableCreatedEvent.class);
    }
}
