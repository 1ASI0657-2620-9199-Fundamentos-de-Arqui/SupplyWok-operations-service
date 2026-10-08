package aurora.supply_wok.platform.iot.application.internal.queryservices;

import aurora.supply_wok.platform.iot.application.queryservices.SensorQueryService;
import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import aurora.supply_wok.platform.iot.domain.model.queries.GetAllSensorsQuery;
import aurora.supply_wok.platform.iot.domain.model.queries.GetSensorByIdQuery;
import aurora.supply_wok.platform.iot.infrastructure.persistence.jpa.repositories.SensorPersistenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the SensorQueryService interface.
 * Handles query-side actions for Sensor entities (find all, find by ID).
 */
@Service
public class SensorQueryServiceImpl implements SensorQueryService {

    private final SensorPersistenceRepository sensorPersistenceRepository;

    public SensorQueryServiceImpl(SensorPersistenceRepository sensorPersistenceRepository) {
        this.sensorPersistenceRepository = sensorPersistenceRepository;
    }

    @Override
    public List<Sensor> handle(GetAllSensorsQuery query) {
        if (query.restaurantId() != null) {
            return this.sensorPersistenceRepository.findAllByRestaurantId(query.restaurantId());
        }
        return this.sensorPersistenceRepository.findAll();
    }

    @Override
    public Optional<Sensor> handle(GetSensorByIdQuery query) {
        if (query.restaurantId() != null) {
            return this.sensorPersistenceRepository.findByIdAndRestaurantId(query.sensorId(), query.restaurantId());
        }
        return this.sensorPersistenceRepository.findById(query.sensorId());
    }
}
