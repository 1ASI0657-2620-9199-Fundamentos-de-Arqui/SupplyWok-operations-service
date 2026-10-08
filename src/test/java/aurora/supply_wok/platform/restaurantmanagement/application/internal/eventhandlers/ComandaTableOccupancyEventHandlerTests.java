package aurora.supply_wok.platform.restaurantmanagement.application.internal.eventhandlers;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.ComandaCreatedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.events.ComandaStatusChangedEvent;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.EComandaStatus;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.ETableStatus;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.RestaurantTableRepository;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComandaTableOccupancyEventHandlerTests {

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ComandaTableOccupancyEventHandler eventHandler;

    @Test
    void onComandaCreated_whenTableExists_marksTableAsOccupiedAndSaves() {
        // Arrange
        var table = new RestaurantTable(1, 4);
        when(tableRepository.findById(5L)).thenReturn(Optional.of(table));
        var event = new ComandaCreatedEvent(10L, 5L, "OPEN");

        // Act
        eventHandler.on(event);

        // Assert
        assertThat(table.getStatus()).isEqualTo(ETableStatus.OCCUPIED);
        verify(tableRepository).save(table);
        verify(domainEventPublisher).publishAndClear(table);
    }

    @Test
    void onComandaCreated_whenTableNotFound_doesNotSave() {
        // Arrange
        when(tableRepository.findById(99L)).thenReturn(Optional.empty());
        var event = new ComandaCreatedEvent(10L, 99L, "OPEN");

        // Act
        eventHandler.on(event);

        // Assert
        verify(tableRepository, never()).save(any());
        verify(domainEventPublisher, never()).publishAndClear(any(RestaurantTable.class));
    }

    @Test
    void onComandaStatusChanged_whenStatusClosed_marksTableAsAvailableAndSaves() {
        // Arrange
        var table = new RestaurantTable(1, 4);
        table.updateStatus(new aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand(1L, ETableStatus.OCCUPIED));
        when(tableRepository.findById(5L)).thenReturn(Optional.of(table));
        var event = new ComandaStatusChangedEvent(10L, 5L, EComandaStatus.SERVED, EComandaStatus.CLOSED);

        // Act
        eventHandler.on(event);

        // Assert
        assertThat(table.getStatus()).isEqualTo(ETableStatus.AVAILABLE);
        verify(tableRepository).save(table);
        verify(domainEventPublisher).publishAndClear(table);
    }

    @Test
    void onComandaStatusChanged_whenStatusNotClosedOrServed_doesNotModifyTable() {
        // Arrange
        var event = new ComandaStatusChangedEvent(10L, 5L, EComandaStatus.OPEN, EComandaStatus.IN_PREPARATION);

        // Act
        eventHandler.on(event);

        // Assert
        verify(tableRepository, never()).findById(any());
        verify(tableRepository, never()).save(any());
    }
}
