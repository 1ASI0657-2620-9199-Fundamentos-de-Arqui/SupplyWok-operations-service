package aurora.supply_wok.platform.alerts.application.internal.commandservices;

import aurora.supply_wok.platform.alerts.domain.model.aggregates.Alert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.RestaurantAlert;
import aurora.supply_wok.platform.alerts.domain.model.aggregates.SupplierAlert;
import aurora.supply_wok.platform.alerts.domain.model.commands.AcknowledgeAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateAlertRestaurantFromInventoryCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateRestaurantAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.commands.CreateSupplierAlertCommand;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertSeverity;
import aurora.supply_wok.platform.alerts.domain.model.valueobjects.EAlertStatus;
import aurora.supply_wok.platform.alerts.infrastructure.clients.InventoryServiceClient;
import aurora.supply_wok.platform.alerts.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import aurora.supply_wok.platform.iot.interfaces.acl.IotContextFacade;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertCommandServiceImplTests {

    @Mock
    private AlertPersistenceRepository alertPersistenceRepository;

    @Mock
    private IotContextFacade iotContextFacade;

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private AlertCommandServiceImpl alertCommandService;

    @Test
    void handleCreateSupplierAlert_whenValidCommand_savesAlertAndPublishesEvent() {
        // Arrange
        var command = new CreateSupplierAlertCommand(EAlertSeverity.HIGH, "Delivery delayed");

        // Act
        Long id = alertCommandService.handle(command);

        // Assert
        verify(alertPersistenceRepository).save(any(SupplierAlert.class));
        verify(domainEventPublisher).publishAndClear(any(SupplierAlert.class));
    }

    @Test
    void handleCreateSupplierAlert_whenRepositorySaveFails_throwsIllegalArgumentException() {
        // Arrange
        var command = new CreateSupplierAlertCommand(EAlertSeverity.HIGH, "Delivery delayed");
        doThrow(new RuntimeException("DB error")).when(alertPersistenceRepository).save(any(SupplierAlert.class));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> alertCommandService.handle(command));
    }

    @Test
    void handleCreateRestaurantAlert_whenSensorIdProvidedAndValid_fetchesSensorNameAndSaves() {
        // Arrange
        when(iotContextFacade.fetchSensorNameById(2L)).thenReturn("Temperature Sensor");
        var command = new CreateRestaurantAlertCommand(EAlertSeverity.MEDIUM, "Temp high", 2L);

        // Act
        Long id = alertCommandService.handle(command);

        // Assert
        verify(alertPersistenceRepository).save(any(RestaurantAlert.class));
        verify(domainEventPublisher).publishAndClear(any(RestaurantAlert.class));
    }

    @Test
    void handleCreateRestaurantAlert_whenSensorIdProvidedButSensorNameBlank_throwsIllegalArgumentException() {
        // Arrange
        when(iotContextFacade.fetchSensorNameById(2L)).thenReturn("");
        var command = new CreateRestaurantAlertCommand(EAlertSeverity.MEDIUM, "Temp high", 2L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> alertCommandService.handle(command));
        verify(alertPersistenceRepository, never()).save(any());
    }

    @Test
    void handleCreateRestaurantAlert_whenSensorIdNull_defaultsToSystemAndSaves() {
        // Arrange
        var command = new CreateRestaurantAlertCommand(EAlertSeverity.LOW, "Manual alert", null);

        // Act
        Long id = alertCommandService.handle(command);

        // Assert
        verify(iotContextFacade, never()).fetchSensorNameById(anyLong());
        verify(alertPersistenceRepository).save(any(RestaurantAlert.class));
        verify(domainEventPublisher).publishAndClear(any(RestaurantAlert.class));
    }

    @Test
    void handleCreateRestaurantAlert_whenRepositoryThrows_throwsIllegalArgumentException() {
        // Arrange
        var command = new CreateRestaurantAlertCommand(EAlertSeverity.LOW, "Manual alert", null);
        doThrow(new RuntimeException("Save failed")).when(alertPersistenceRepository).save(any(RestaurantAlert.class));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> alertCommandService.handle(command));
    }

    @Test
    void handleCreateAlertRestaurantFromInventory_whenStockDiffersFromSensor_createsAndSavesAlert() {
        // Arrange
        when(iotContextFacade.fetchSensorLastValueById(5L)).thenReturn(Optional.of(10.0));
        when(iotContextFacade.fetchSensorNameById(5L)).thenReturn("Scale Sensor");
        when(inventoryServiceClient.getTotalSupplyStock()).thenReturn(5L);
        var command = new CreateAlertRestaurantFromInventoryCommand(5L);

        // Act
        Optional<Alert> result = alertCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        verify(alertPersistenceRepository).save(any(RestaurantAlert.class));
        verify(domainEventPublisher).publishAndClear(any(RestaurantAlert.class));
    }

    @Test
    void handleCreateAlertRestaurantFromInventory_whenStockMatchesSensor_returnsEmptyOptional() {
        // Arrange
        when(iotContextFacade.fetchSensorLastValueById(5L)).thenReturn(Optional.of(5.0));
        when(iotContextFacade.fetchSensorNameById(5L)).thenReturn("Scale Sensor");
        when(inventoryServiceClient.getTotalSupplyStock()).thenReturn(5L);
        var command = new CreateAlertRestaurantFromInventoryCommand(5L);

        // Act
        Optional<Alert> result = alertCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(alertPersistenceRepository, never()).save(any());
        verify(domainEventPublisher, never()).publishAndClear(any(Alert.class));
    }

    @Test
    void handleCreateAlertRestaurantFromInventory_whenSensorNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(iotContextFacade.fetchSensorLastValueById(5L)).thenReturn(Optional.empty());
        var command = new CreateAlertRestaurantFromInventoryCommand(5L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> alertCommandService.handle(command));
        verify(alertPersistenceRepository, never()).save(any());
    }

    @Test
    void handleAcknowledgeAlert_whenAlertExists_acknowledgesSavesAndReturnsAlert() {
        // Arrange
        var alert = new RestaurantAlert(EAlertSeverity.HIGH, "Warning", 1L, "Sensor 1");
        when(alertPersistenceRepository.findById(10L)).thenReturn(Optional.of(alert));
        var command = new AcknowledgeAlertCommand(10L);

        // Act
        Optional<Alert> result = alertCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(EAlertStatus.ACKNOWLEDGED);
        verify(alertPersistenceRepository).save(alert);
        verify(domainEventPublisher).publishAndClear(alert);
    }

    @Test
    void handleAcknowledgeAlert_whenAlertNotFound_returnsEmptyOptional() {
        // Arrange
        when(alertPersistenceRepository.findById(99L)).thenReturn(Optional.empty());
        var command = new AcknowledgeAlertCommand(99L);

        // Act
        Optional<Alert> result = alertCommandService.handle(command);

        // Assert
        assertThat(result).isEmpty();
        verify(alertPersistenceRepository, never()).save(any());
    }

    @Test
    void handleAcknowledgeAlert_whenRepositorySaveThrows_throwsIllegalArgumentException() {
        // Arrange
        var alert = new RestaurantAlert(EAlertSeverity.HIGH, "Warning", 1L, "Sensor 1");
        when(alertPersistenceRepository.findById(10L)).thenReturn(Optional.of(alert));
        doThrow(new RuntimeException("DB write error")).when(alertPersistenceRepository).save(any());
        var command = new AcknowledgeAlertCommand(10L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> alertCommandService.handle(command));
    }
}
