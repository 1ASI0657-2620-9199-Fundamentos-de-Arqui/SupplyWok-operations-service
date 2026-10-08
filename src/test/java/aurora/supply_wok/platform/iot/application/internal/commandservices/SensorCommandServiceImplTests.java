package aurora.supply_wok.platform.iot.application.internal.commandservices;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import aurora.supply_wok.platform.iot.domain.model.commands.CreateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.commands.DeleteSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.commands.UpdateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import aurora.supply_wok.platform.iot.infrastructure.persistence.jpa.repositories.SensorPersistenceRepository;
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
class SensorCommandServiceImplTests {

    @Mock
    private SensorPersistenceRepository sensorPersistenceRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private SensorCommandServiceImpl sensorCommandService;

    @Test
    void handleCreateSensor_whenValidCommand_savesSensorAndPublishesDomainEvent() {
        // Arrange
        var command = new CreateSensorCommand("Thermometer", 0.0, 100.0, true, 25.0, ESensorType.Temperature);

        // Act
        Long result = sensorCommandService.handle(command);

        // Assert
        verify(sensorPersistenceRepository).save(any(Sensor.class));
        verify(domainEventPublisher).publishAndClear(any(Sensor.class));
    }

    @Test
    void handleCreateSensor_whenSaveThrowsException_throwsIllegalArgumentException() {
        // Arrange
        var command = new CreateSensorCommand("Thermometer", 0.0, 100.0, true, 25.0, ESensorType.Temperature);
        doThrow(new RuntimeException("DB fail")).when(sensorPersistenceRepository).save(any(Sensor.class));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sensorCommandService.handle(command));
    }

    @Test
    void handleUpdateSensor_whenSensorExists_updatesSensorSavesAndReturnsOptional() {
        // Arrange
        var sensor = new Sensor("Thermometer", 0.0, 100.0, true, 25.0, ESensorType.Temperature);
        when(sensorPersistenceRepository.findById(1L)).thenReturn(Optional.of(sensor));
        var command = new UpdateSensorCommand(1L, "New Thermometer", -10.0, 90.0, true, 30.0, ESensorType.Temperature);

        // Act
        Optional<Sensor> result = sensorCommandService.handle(command);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Thermometer");
        verify(sensorPersistenceRepository).save(sensor);
        verify(domainEventPublisher).publishAndClear(sensor);
    }

    @Test
    void handleUpdateSensor_whenSensorNotFound_throwsIllegalArgumentException() {
        // Arrange
        when(sensorPersistenceRepository.findById(99L)).thenReturn(Optional.empty());
        var command = new UpdateSensorCommand(99L, "New Thermometer", -10.0, 90.0, true, 30.0, ESensorType.Temperature);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sensorCommandService.handle(command));
        verify(sensorPersistenceRepository, never()).save(any());
    }

    @Test
    void handleUpdateSensor_whenSaveThrowsException_throwsIllegalArgumentException() {
        // Arrange
        var sensor = new Sensor("Thermometer", 0.0, 100.0, true, 25.0, ESensorType.Temperature);
        when(sensorPersistenceRepository.findById(1L)).thenReturn(Optional.of(sensor));
        doThrow(new RuntimeException("DB fail")).when(sensorPersistenceRepository).save(any(Sensor.class));
        var command = new UpdateSensorCommand(1L, "New Thermometer", -10.0, 90.0, true, 30.0, ESensorType.Temperature);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sensorCommandService.handle(command));
    }

    @Test
    void handleDeleteSensor_whenSensorExists_deletesFromRepository() {
        // Arrange
        when(sensorPersistenceRepository.existsById(1L)).thenReturn(true);
        var command = new DeleteSensorCommand(1L);

        // Act
        sensorCommandService.handle(command);

        // Assert
        verify(sensorPersistenceRepository).deleteById(1L);
    }

    @Test
    void handleDeleteSensor_whenSensorDoesNotExist_throwsIllegalArgumentException() {
        // Arrange
        when(sensorPersistenceRepository.existsById(99L)).thenReturn(false);
        var command = new DeleteSensorCommand(99L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sensorCommandService.handle(command));
        verify(sensorPersistenceRepository, never()).deleteById(anyLong());
    }

    @Test
    void handleDeleteSensor_whenDeleteThrowsException_throwsIllegalArgumentException() {
        // Arrange
        when(sensorPersistenceRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("DB error")).when(sensorPersistenceRepository).deleteById(1L);
        var command = new DeleteSensorCommand(1L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sensorCommandService.handle(command));
    }
}
