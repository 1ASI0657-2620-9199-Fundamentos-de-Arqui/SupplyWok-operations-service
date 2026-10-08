package aurora.supply_wok.platform.iot.application.internal.commandservices;

import aurora.supply_wok.platform.iot.application.commandservices.SensorCommandService;
import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import aurora.supply_wok.platform.iot.domain.model.commands.CreateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.commands.DeleteSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.commands.UpdateSensorCommand;
import aurora.supply_wok.platform.iot.infrastructure.persistence.jpa.repositories.SensorPersistenceRepository;
import aurora.supply_wok.platform.shared.domain.exceptions.ResourceNotFoundException;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the SensorCommandService interface.
 * Handles command-side actions for Sensor entities (create, update, delete).
 */
@Service
public class SensorCommandServiceImpl implements SensorCommandService {

    private final SensorPersistenceRepository sensorPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public SensorCommandServiceImpl(SensorPersistenceRepository sensorPersistenceRepository,
                                    DomainEventPublisher domainEventPublisher) {
        this.sensorPersistenceRepository = sensorPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Long handle(CreateSensorCommand command) {
        var sensor = new Sensor(command);
        try {
            sensorPersistenceRepository.save(sensor);
            sensor.onCreated();
            domainEventPublisher.publishAndClear(sensor);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while saving sensor: " + e.getMessage());
        }
        return sensor.getId();
    }

    @Override
    public Optional<Sensor> handle(UpdateSensorCommand command) {
        var result = command.restaurantId() != null
                ? sensorPersistenceRepository.findByIdAndRestaurantId(command.sensorId(), command.restaurantId())
                : sensorPersistenceRepository.findById(command.sensorId());

        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Sensor with ID " + command.sensorId() + " does not exist");
        }

        var sensor = result.get();
        sensor.updateSensor(command);
        try {
            sensorPersistenceRepository.save(sensor);
            domainEventPublisher.publishAndClear(sensor);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while updating sensor: " + e.getMessage());
        }
        return Optional.of(sensor);
    }

    @Override
    public void handle(DeleteSensorCommand command) {
        boolean exists = command.restaurantId() != null
                ? sensorPersistenceRepository.existsByIdAndRestaurantId(command.sensorId(), command.restaurantId())
                : sensorPersistenceRepository.existsById(command.sensorId());

        if (!exists) {
            throw new ResourceNotFoundException("Sensor with ID " + command.sensorId() + " does not exist");
        }

        try {
            sensorPersistenceRepository.deleteById(command.sensorId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while deleting sensor: " + e.getMessage());
        }
    }
}
