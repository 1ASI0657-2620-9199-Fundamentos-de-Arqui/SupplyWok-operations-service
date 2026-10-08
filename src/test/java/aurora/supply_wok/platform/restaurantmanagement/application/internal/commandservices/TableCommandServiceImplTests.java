package aurora.supply_wok.platform.restaurantmanagement.application.internal.commandservices;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.RestaurantTable;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.DeleteTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateTableStatusCommand;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableCommandServiceImplTests {

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private TableCommandServiceImpl tableCommandService;

    @Test
    void handleCreateTable_whenValidCommand_savesTableAndPublishesDomainEvent() {
        // Arrange
        var command = new CreateTableCommand(5, 4);

        // Act
        Long result = tableCommandService.handle(command);

        // Assert
        verify(tableRepository).save(any(RestaurantTable.class));
        verify(domainEventPublisher).publishAndClear(any(RestaurantTable.class));
    }

    @Test
    void handleUpdateTable_whenTableExists_updatesTableSavesAndReturnsTable() {
        // Arrange
        var table = new RestaurantTable(5, 4);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        var command = new UpdateTableCommand(1L, 6, 8);

        // Act
        Optional<RestaurantTable> result = tableCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getNumber()).isEqualTo(6);
        assertThat(result.get().getCapacity()).isEqualTo(8);
        verify(tableRepository).save(table);
        verify(domainEventPublisher).publishAndClear(table);
    }

    @Test
    void handleUpdateTable_whenTableNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(tableRepository.findById(99L)).thenReturn(Optional.empty());
        var command = new UpdateTableCommand(99L, 6, 8);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tableCommandService.handle(command));
        verify(tableRepository, never()).save(any());
        verify(domainEventPublisher, never()).publishAndClear(any(RestaurantTable.class));
    }

    @Test
    void handleUpdateTableStatus_whenTableExists_updatesStatusSavesAndReturnsTable() {
        // Arrange
        var table = new RestaurantTable(5, 4);
        when(tableRepository.findById(1L)).thenReturn(Optional.of(table));
        var command = new UpdateTableStatusCommand(1L, ETableStatus.RESERVED);

        // Act
        Optional<RestaurantTable> result = tableCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(ETableStatus.RESERVED);
        verify(tableRepository).save(table);
        verify(domainEventPublisher).publishAndClear(table);
    }

    @Test
    void handleUpdateTableStatus_whenTableNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(tableRepository.findById(99L)).thenReturn(Optional.empty());
        var command = new UpdateTableStatusCommand(99L, ETableStatus.RESERVED);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tableCommandService.handle(command));
        verify(tableRepository, never()).save(any());
        verify(domainEventPublisher, never()).publishAndClear(any(RestaurantTable.class));
    }

    @Test
    void handleDeleteTable_whenTableExists_deletesFromRepository() {
        // Arrange
        when(tableRepository.existsById(1L)).thenReturn(true);
        var command = new DeleteTableCommand(1L);

        // Act
        tableCommandService.handle(command);

        // Assert
        verify(tableRepository).deleteById(1L);
    }

    @Test
    void handleDeleteTable_whenTableNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(tableRepository.existsById(99L)).thenReturn(false);
        var command = new DeleteTableCommand(99L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tableCommandService.handle(command));
        verify(tableRepository, never()).deleteById(anyLong());
    }
}
