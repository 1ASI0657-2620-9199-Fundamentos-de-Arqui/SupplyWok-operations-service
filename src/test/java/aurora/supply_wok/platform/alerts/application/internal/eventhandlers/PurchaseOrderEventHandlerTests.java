package aurora.supply_wok.platform.alerts.application.internal.eventhandlers;

import aurora.supply_wok.platform.alerts.application.commandservices.AlertCommandService;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateSupplierAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderCreatedIntegrationEvent;
import aurora.supply_wok.platform.purchasing.interfaces.events.PurchaseOrderStatusChangedIntegrationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderEventHandlerTests {

    @Mock
    private AlertCommandService alertCommandService;

    @InjectMocks
    private PurchaseOrderEventHandler eventHandler;

    @Test
    void onPurchaseOrderCreated_whenPriorityHigh_createsHighSeveritySupplierAlert() {
        // Arrange
        var event = new PurchaseOrderCreatedIntegrationEvent(
                1L, "PO-100", 2L, "Supplier A", "Rest Wok", "High", "PENDING"
        );

        // Act
        eventHandler.on(event);

        // Assert
        var captor = ArgumentCaptor.forClass(CreateSupplierAlertCommand.class);
        verify(alertCommandService).handle(captor.capture());
        assertThat(captor.getValue().severity()).isEqualTo(EAlertSeverity.HIGH);
        assertThat(captor.getValue().detail()).contains("PO-100");
    }

    @Test
    void onPurchaseOrderCreated_whenPriorityNotHigh_createsMediumSeveritySupplierAlert() {
        // Arrange
        var event = new PurchaseOrderCreatedIntegrationEvent(
                1L, "PO-101", 2L, "Supplier A", "Rest Wok", "Normal", "PENDING"
        );

        // Act
        eventHandler.on(event);

        // Assert
        var captor = ArgumentCaptor.forClass(CreateSupplierAlertCommand.class);
        verify(alertCommandService).handle(captor.capture());
        assertThat(captor.getValue().severity()).isEqualTo(EAlertSeverity.MEDIUM);
    }

    @Test
    void onPurchaseOrderStatusChanged_whenStatusDelayed_createsHighSeverityRestaurantAlert() {
        // Arrange
        var event = new PurchaseOrderStatusChangedIntegrationEvent(
                1L, "PO-102", 2L, "Supplier B", "Rest Wok", "CONFIRMED", "Delayed"
        );

        // Act
        eventHandler.on(event);

        // Assert
        var captor = ArgumentCaptor.forClass(CreateRestaurantAlertCommand.class);
        verify(alertCommandService).handle(captor.capture());
        assertThat(captor.getValue().severity()).isEqualTo(EAlertSeverity.HIGH);
        assertThat(captor.getValue().detail()).contains("PO-102").contains("delayed");
    }

    @Test
    void onPurchaseOrderStatusChanged_whenStatusDelivered_createsLowSeverityRestaurantAlert() {
        // Arrange
        var event = new PurchaseOrderStatusChangedIntegrationEvent(
                1L, "PO-103", 2L, "Supplier B", "Rest Wok", "SHIPPED", "Delivered"
        );

        // Act
        eventHandler.on(event);

        // Assert
        var captor = ArgumentCaptor.forClass(CreateRestaurantAlertCommand.class);
        verify(alertCommandService).handle(captor.capture());
        assertThat(captor.getValue().severity()).isEqualTo(EAlertSeverity.LOW);
        assertThat(captor.getValue().detail()).contains("PO-103").contains("delivered");
    }

    @Test
    void onPurchaseOrderStatusChanged_whenStatusOther_doesNotCreateAlert() {
        // Arrange
        var event = new PurchaseOrderStatusChangedIntegrationEvent(
                1L, "PO-104", 2L, "Supplier B", "Rest Wok", "PENDING", "PROCESSING"
        );

        // Act
        eventHandler.on(event);

        // Assert
        verify(alertCommandService, never()).handle(any(CreateRestaurantAlertCommand.class));
    }
}
