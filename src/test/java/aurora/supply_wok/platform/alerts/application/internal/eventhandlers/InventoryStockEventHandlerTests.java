package aurora.supply_wok.platform.alerts.application.internal.eventhandlers;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.inventory.interfaces.events.SupplyMinimumStockReachedIntegrationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryStockEventHandlerTests {

    @Mock
    private AlertCommandService alertCommandService;

    @InjectMocks
    private InventoryStockEventHandler eventHandler;

    @Test
    void on_whenStockReachedEventReceived_delegatesToAlertCommandService() {
        // Arrange
        var event = new SupplyMinimumStockReachedIntegrationEvent(1L, "Soy Sauce", 2, 10);

        // Act
        eventHandler.on(event);

        // Assert
        verify(alertCommandService).handle(any(CreateRestaurantAlertCommand.class));
    }

    @Test
    void on_whenCommandServiceThrowsException_logsAndDoesNotRethrow() {
        // Arrange
        var event = new SupplyMinimumStockReachedIntegrationEvent(1L, "Soy Sauce", 2, 10);
        doThrow(new RuntimeException("Service down")).when(alertCommandService).handle(any(CreateRestaurantAlertCommand.class));

        // Act
        eventHandler.on(event);

        // Assert
        verify(alertCommandService).handle(any(CreateRestaurantAlertCommand.class));
    }
}
