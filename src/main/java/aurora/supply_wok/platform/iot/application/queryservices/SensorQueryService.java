package aurora.supply_wok.platform.iot.application.queryservices;

import aurora.supply_wok.platform.iot.domain.model.aggregates.Sensor;
import aurora.supply_wok.platform.iot.domain.model.queries.GetAllSensorsQuery;
import aurora.supply_wok.platform.iot.domain.model.queries.GetSensorByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling sensor-related queries.
 */
public interface SensorQueryService {
    List<Sensor> handle(GetAllSensorsQuery query);
    Optional<Sensor> handle(GetSensorByIdQuery query);
}
