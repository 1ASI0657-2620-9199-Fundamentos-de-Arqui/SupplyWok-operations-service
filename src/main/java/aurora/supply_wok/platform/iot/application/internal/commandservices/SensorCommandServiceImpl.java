package aurora.supply_wok.platform.iot.application.internal.commandservices;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import aurora.supply_wok.platform.iot.domain.model.commands.CreateSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.commands.DeleteSensorCommand;
import aurora.supply_wok.platform.iot.domain.model.commands.UpdateSensorCommand;
import aurora.supply_wok.platform.iot.application.commandservices.SensorCommandService;
import aurora.supply_wok.platform.iot.infrastructure.persistence.jpa.repositories.SensorPersistenceRepository;
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

    /**
     * Constructs the SensorCommandServiceImpl.
     *
     * @param sensorPersistenceRepository the repository for Sensor persistence
     */
    public SensorCommandServiceImpl(SensorPersistenceRepository sensorPersistenceRepository,
                                    DomainEventPublisher domainEventPublisher) {
        this.sensorPersistenceRepository = sensorPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    /**
     * Handles the creation of a new Sensor.
     *
     * @param command createSensorCommand containing sensor details
     * @return the ID of the newly created Sensor
     * @throws IllegalArgumentException if an error occurs while saving the sensor
     */
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

    /**
     * Handles the update of an existing Sensor.
     *
     * @param command the UpdateSensorCommand containing updated sensor details
     * @return an Optional containing the updated Sensor if found and saved, otherwise empty
     * @throws IllegalArgumentException if the sensor does not exist or an error occurs during save
     */
    @Override
    public Optional<Sensor> handle(UpdateSensorCommand command) {
        var result = sensorPersistenceRepository.findById(command.sensorId());
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Sensor with ID " + command.sensorId() + " does not exist");
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

    /**
     * Handles the deletion of a Sensor.
     *
     * @param command the DeleteSensorCommand containing the sensor ID to delete
     * @throws IllegalArgumentException if the sensor does not exist or an error occurs during deletion
     */
    @Override
    public void handle(DeleteSensorCommand command) {
        if (!sensorPersistenceRepository.existsById(command.sensorId())) {
            throw new IllegalArgumentException("Sensor with ID " + command.sensorId() + " does not exist");
        }
        try {
            sensorPersistenceRepository.deleteById(command.sensorId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while deleting sensor: " + e.getMessage());
        }
    }
}
