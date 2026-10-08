package aurora.supply_wok.platform.iot.application.internal.queryservices;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import aurora.supply_wok.platform.iot.domain.model.queries.GetAllSensorsQuery;
import aurora.supply_wok.platform.iot.domain.model.queries.GetSensorByIdQuery;
import aurora.supply_wok.platform.iot.domain.model.valueobjects.ESensorType;
import aurora.supply_wok.platform.iot.infrastructure.persistence.jpa.repositories.SensorPersistenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorQueryServiceImplTests {

    @Mock
    private SensorPersistenceRepository sensorPersistenceRepository;

    @InjectMocks
    private SensorQueryServiceImpl sensorQueryService;

    @Test
    void handleGetAllSensors_whenInvoked_delegatesToRepositoryFindAll() {
        // Arrange
        var expectedList = List.of(new Sensor("S1", 0.0, 10.0, true, 5.0, ESensorType.Temperature));
        when(sensorPersistenceRepository.findAll()).thenReturn(expectedList);

        // Act
        List<Sensor> result = sensorQueryService.handle(new GetAllSensorsQuery());

        // Assert
        assertThat(result).isSameAs(expectedList);
        verify(sensorPersistenceRepository).findAll();
    }

    @Test
    void handleGetSensorById_whenInvoked_delegatesToRepositoryFindById() {
        // Arrange
        var sensor = new Sensor("S1", 0.0, 10.0, true, 5.0, ESensorType.Temperature);
        when(sensorPersistenceRepository.findById(7L)).thenReturn(Optional.of(sensor));

        // Act
        Optional<Sensor> result = sensorQueryService.handle(new GetSensorByIdQuery(7L));

        // Assert
        assertThat(result).containsSame(sensor);
        verify(sensorPersistenceRepository).findById(7L);
    }
}
