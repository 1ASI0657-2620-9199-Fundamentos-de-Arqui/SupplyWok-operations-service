package aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.AddComandaItemCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateComandaCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateComandaStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.ComandaCreatedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.ComandaItemAddedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.ComandaStatusChangedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.EComandaStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComandaTests {

    @Test
    void constructor_withTableId_initializesWithOpenStatusAndEmptyItems() {
        // Arrange
        Long tableId = 10L;

        // Act
        var comanda = new Comanda(tableId);

        // Assert
        assertThat(comanda.getTableId()).isEqualTo(tableId);
        assertThat(comanda.getStatus()).isEqualTo(EComandaStatus.OPEN);
        assertThat(comanda.getItems()).isEmpty();
    }

    @Test
    void constructor_withCreateComandaCommand_initializesWithTableIdAndOpenStatus() {
        // Arrange
        var command = new CreateComandaCommand(5L);

        // Act
        var comanda = new Comanda(command);

        // Assert
        assertThat(comanda.getTableId()).isEqualTo(5L);
        assertThat(comanda.getStatus()).isEqualTo(EComandaStatus.OPEN);
        assertThat(comanda.getItems()).isEmpty();
    }

    @Test
    void updateStatus_whenStatusDiffers_updatesStatusAndRegistersDomainEvent() {
        // Arrange
        var comanda = new Comanda(1L);
        var command = new UpdateComandaStatusCommand(1L, EComandaStatus.IN_PREPARATION);

        // Act
        comanda.updateStatus(command);

        // Assert
        assertThat(comanda.getStatus()).isEqualTo(EComandaStatus.IN_PREPARATION);
        assertThat(comanda.domainEvents()).hasSize(1);
        assertThat(comanda.domainEvents().iterator().next()).isInstanceOf(ComandaStatusChangedEvent.class);
    }

    @Test
    void updateStatus_whenStatusIsIdentical_doesNotRegisterStatusChangedEvent() {
        // Arrange
        var comanda = new Comanda(1L);
        var command = new UpdateComandaStatusCommand(1L, EComandaStatus.OPEN);

        // Act
        comanda.updateStatus(command);

        // Assert
        assertThat(comanda.getStatus()).isEqualTo(EComandaStatus.OPEN);
        assertThat(comanda.domainEvents()).isEmpty();
    }

    @Test
    void addItem_whenValidCommand_addsItemAndRegistersDomainEvent() {
        // Arrange
        var comanda = new Comanda(1L);
        var command = new AddComandaItemCommand(1L, "Wonton Soup", 2, "Extra crispy");

        // Act
        var item = comanda.addItem(command);

        // Assert
        assertThat(comanda.getItems()).containsExactly(item);
        assertThat(item.getProductName()).isEqualTo("Wonton Soup");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getNotes()).isEqualTo("Extra crispy");
        assertThat(comanda.domainEvents()).hasSize(1);
        assertThat(comanda.domainEvents().iterator().next()).isInstanceOf(ComandaItemAddedEvent.class);
    }

    @Test
    void sendToKitchen_whenStatusIsOpen_updatesStatusToSentToKitchenAndRegistersEvent() {
        // Arrange
        var comanda = new Comanda(1L);

        // Act
        comanda.sendToKitchen();

        // Assert
        assertThat(comanda.getStatus()).isEqualTo(EComandaStatus.SENT_TO_KITCHEN);
        assertThat(comanda.domainEvents()).hasSize(1);
        assertThat(comanda.domainEvents().iterator().next()).isInstanceOf(ComandaStatusChangedEvent.class);
    }

    @Test
    void sendToKitchen_whenAlreadySentToKitchen_doesNotRegisterNewStatusEvent() {
        // Arrange
        var comanda = new Comanda(1L);
        comanda.sendToKitchen();
        comanda.clearDomainEvents();

        // Act
        comanda.sendToKitchen();

        // Assert
        assertThat(comanda.getStatus()).isEqualTo(EComandaStatus.SENT_TO_KITCHEN);
        assertThat(comanda.domainEvents()).isEmpty();
    }

    @Test
    void close_whenStatusIsNotClosed_updatesStatusToClosedAndRegistersEvent() {
        // Arrange
        var comanda = new Comanda(1L);

        // Act
        comanda.close();

        // Assert
        assertThat(comanda.getStatus()).isEqualTo(EComandaStatus.CLOSED);
        assertThat(comanda.domainEvents()).hasSize(1);
        assertThat(comanda.domainEvents().iterator().next()).isInstanceOf(ComandaStatusChangedEvent.class);
    }

    @Test
    void close_whenAlreadyClosed_doesNotRegisterNewStatusEvent() {
        // Arrange
        var comanda = new Comanda(1L);
        comanda.close();
        comanda.clearDomainEvents();

        // Act
        comanda.close();

        // Assert
        assertThat(comanda.getStatus()).isEqualTo(EComandaStatus.CLOSED);
        assertThat(comanda.domainEvents()).isEmpty();
    }

    @Test
    void onCreated_whenCalled_registersComandaCreatedEvent() {
        // Arrange
        var comanda = new Comanda(1L);

        // Act
        comanda.onCreated();

        // Assert
        assertThat(comanda.domainEvents()).hasSize(1);
        assertThat(comanda.domainEvents().iterator().next()).isInstanceOf(ComandaCreatedEvent.class);
    }
}
