package aurora.supply_wok.platform.restaurantmanagement.application.internal.commandservices;

import aurora.supply_wok.platform.restaurantmanagement.domain.model.aggregates.Comanda;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.AddComandaItemCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.CreateComandaCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.DeleteComandaCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.commands.UpdateComandaStatusCommand;
import aurora.supply_wok.platform.restaurantmanagement.domain.model.valueobjects.EComandaStatus;
import aurora.supply_wok.platform.restaurantmanagement.infrastructure.persistence.jpa.repositories.ComandaRepository;
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
class ComandaCommandServiceImplTests {

    @Mock
    private ComandaRepository comandaRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ComandaCommandServiceImpl comandaCommandService;

    @Test
    void handleCreateComanda_whenValidCommand_savesComandaAndPublishesDomainEvent() {
        // Arrange
        var command = new CreateComandaCommand(3L);

        // Act
        Long result = comandaCommandService.handle(command);

        // Assert
        verify(comandaRepository).save(any(Comanda.class));
        verify(domainEventPublisher).publishAndClear(any(Comanda.class));
    }

    @Test
    void handleUpdateComandaStatus_whenComandaExists_updatesStatusSavesAndReturnsComanda() {
        // Arrange
        var comanda = new Comanda(3L);
        when(comandaRepository.findById(10L)).thenReturn(Optional.of(comanda));
        var command = new UpdateComandaStatusCommand(10L, EComandaStatus.SERVED);

        // Act
        Optional<Comanda> result = comandaCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo(EComandaStatus.SERVED);
        verify(comandaRepository).save(comanda);
        verify(domainEventPublisher).publishAndClear(comanda);
    }

    @Test
    void handleUpdateComandaStatus_whenComandaNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(comandaRepository.findById(99L)).thenReturn(Optional.empty());
        var command = new UpdateComandaStatusCommand(99L, EComandaStatus.SERVED);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> comandaCommandService.handle(command));
        verify(comandaRepository, never()).save(any());
        verify(domainEventPublisher, never()).publishAndClear(any(Comanda.class));
    }

    @Test
    void handleAddComandaItem_whenComandaExists_addsItemSavesAndReturnsComanda() {
        // Arrange
        var comanda = new Comanda(3L);
        when(comandaRepository.findById(10L)).thenReturn(Optional.of(comanda));
        var command = new AddComandaItemCommand(10L, "Fried Rice", 1, "No onions");

        // Act
        Optional<Comanda> result = comandaCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getItems()).hasSize(1);
        verify(comandaRepository).save(comanda);
        verify(domainEventPublisher).publishAndClear(comanda);
    }

    @Test
    void handleAddComandaItem_whenComandaNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(comandaRepository.findById(99L)).thenReturn(Optional.empty());
        var command = new AddComandaItemCommand(99L, "Fried Rice", 1, "No onions");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> comandaCommandService.handle(command));
        verify(comandaRepository, never()).save(any());
        verify(domainEventPublisher, never()).publishAndClear(any(Comanda.class));
    }

    @Test
    void handleDeleteComanda_whenComandaExists_deletesFromRepository() {
        // Arrange
        when(comandaRepository.existsById(10L)).thenReturn(true);
        var command = new DeleteComandaCommand(10L);

        // Act
        comandaCommandService.handle(command);

        // Assert
        verify(comandaRepository).deleteById(10L);
    }

    @Test
    void handleDeleteComanda_whenComandaDoesNotExist_throwsIllegalArgumentException() {
        // Arrange
        when(comandaRepository.existsById(99L)).thenReturn(false);
        var command = new DeleteComandaCommand(99L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> comandaCommandService.handle(command));
        verify(comandaRepository, never()).deleteById(anyLong());
    }
}
